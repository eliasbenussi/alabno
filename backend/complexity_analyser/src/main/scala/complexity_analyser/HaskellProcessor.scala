package complexity_analyser

import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.concurrent.{Callable, Executors}

import json_parser.Error

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.sys.process.{ProcessLogger, _}

class HaskellProcessor(modelAnswer: File, studentAnswer: File) {

  /*
   * Regexes
   */
  // Matches "benchmarking tests/word"
  private final val BenchmarkLine =
  """benchmarking tests/(\w+)""".r
  // Matches "number.number"
  private final val matchMean =
  """(\d+.\d+)""".r
  // Used to find test names and scores
  // Matches "word: number / number"
  private final val TestLine =
  """(\w+): (\d+) / (\d+)""".r
  // Used to find functions in files
  // Matches "word some whitespace :: something else"
  private final val FunctionLine =
  """(\w+)\s+::\s+.+""".r

  // Map that stores test name and the max score that you can get (taken from model solution)
  private final val TestScore = new mutable.HashMap[String, Int]

  // Map that stores function name, line and file where it is located
  private final val FunctionMap = new mutable.HashMap[String, (Int, String)]

  // Used to run the compilations and the benchmarks
  private final val eP = Executors.newFixedThreadPool(2)

  /**
    * Copies Bench.hs to both model solution and student submission
    * Finds all the functions in the student submission
    * so that later we can trace them back
    * Copies the Model test suite to the student, just in case they changed it
    */
  def prepare(): Unit = {
    val benchFile = "/Bench.hs"
    val tests = "/Tests.hs"
    val bench = new File(getClass.getResource(benchFile).toURI)
    if (!bench.exists()) throw new Exception("Missing resource Bench.hs")
    if (!modelAnswer.isDirectory) throw new Exception("Model solution should be a directory")
    if (!studentAnswer.isDirectory) throw new Exception("Student submission should be a directory")
    val mod = new File(modelAnswer.toPath.toString + benchFile)
    val modTest = new File(modelAnswer.toPath.toString + tests).toPath
    val studTest = new File(studentAnswer.toPath.toString + tests).toPath
    val stud = new File(studentAnswer.toPath.toString + benchFile)
    studentAnswer.listFiles().filter(hFilter).foreach(findFunctions)
    // Ensure that both versions have the same Tests.hs file
    copy(modTest, studTest, REPLACE_EXISTING)
    copy(bench.toPath, mod.toPath, REPLACE_EXISTING)
    copy(bench.toPath, stud.toPath, REPLACE_EXISTING)
  }

  def findFunctions(file: File) = {
    for ((l, i) <- Source.fromFile(file).getLines().zipWithIndex) {
      l match {
        case FunctionLine(n) =>
          FunctionMap += ((n, (i + 1, file.toString)))
        case _ => None
      }
    }
  }

  private def hFilter(f: File) = f.isFile && f.getName.endsWith(".hs") && f.getName != "Tests.hs"

  def runTests() = {
    compileClassOnBoth("Tests")
    val testOutcomeStudent = eP.submit(new Caller(s"$studentAnswer/Tests"))
    val testOutcomeModel = eP.submit(new Caller(s"$modelAnswer/Tests"))
    testOutcomeModel.get._1.split("\n").foreach(findMaxScoreHeader)
    calculateTestScores(findStudentScore(testOutcomeStudent.get._1))
  }

  private def findMaxScoreHeader(line: String): Unit = {
    line match {
      case TestLine(name, _, max) =>
        TestScore += ((name, max.toInt))
      case _ => None
    }
  }

  private def findStudentScore(line: String) = {
    val buff = new ArrayBuffer[(String, Int, Int)]()
    val lines = line.split("\n")
    for (l <- lines) {
      l match {
        case TestLine(name, score, m) =>
          val max = TestScore.getOrElse(name, m.toInt)
          buff += ((name, score.toInt, max))
        case _ => None
      }
    }
    buff
  }

  private def calculateTestScores(testsResult: Seq[(String, Int, Int)]) = {
    var score: Double = 100.0d
    val scorePerTest = Math.round(score / testsResult.length)

    val buff = new ArrayBuffer[Error]
    for ((name, studScore, maxScore) <- testsResult) {
      if (studScore < maxScore) {
        score -= scorePerTest * (1 - (studScore / maxScore))
        val (line, file) = FunctionMap.getOrElse(name, (0, studentAnswer.getName))
        buff += new Error(s"Student passes $studScore/$maxScore tests for $name", file, line, 0, "tests")
      }
    }
    (buff, Math.max(score, 0))
  }

  def runBench() = {
    compileClassOnBoth("Bench")
    val benchOutcomeStudent = eP.submit(new Caller(s"$studentAnswer/Bench ${bFlags(studentAnswer)}"))
    val benchOutcomeModel = eP.submit(new Caller(s"$modelAnswer/Bench ${bFlags(modelAnswer)}"))
    val zippedMeanModel = genListBenchNameMean(benchOutcomeModel.get._1)
    val zippedMeanStud = genListBenchNameMean(benchOutcomeStudent.get._1)
    val deltas = produceDelta(zippedMeanModel, zippedMeanStud)
    calculateScore(deltas)
  }

  private def compileClassOnBoth(name: String) = {
    val exitModel = eP.submit(new Caller(s"ghc -i$modelAnswer/IC -i$modelAnswer " +
      s"--make -O3 $name -main-is $name"))

    val exitStudent = eP.submit(new Caller(s"ghc -i$studentAnswer/IC -i$studentAnswer " +
      s"--make -O3 $name -main-is $name"))

    val (outputModel, returnModel) = exitModel.get
    val (outputStudent, returnStudent) = exitStudent.get
    if (returnModel != 0) throw new IllegalStateException("Model solution did not compile")
    if (returnStudent != 0) throw new IllegalStateException(s"Student answer didn't compile")
    (outputModel, outputStudent)
  }

  private final def bFlags(o: File) = s"--output=$o/res.html"

  private def produceDelta(zippedMeanModel: Seq[(String, Double)], zippedMeanStud: Seq[(String, Double)]) = {
    val buff = new ArrayBuffer[(String, Double)]
    for ((e, i) <- zippedMeanModel.zipWithIndex) {
      val (name, modMean) = e
      val (_, studMean) = zippedMeanStud.apply(i)
      buff += ((name, modMean - studMean))
    }
    buff
  }

  private def genListBenchNameMean(outcome: String) = {
    val names = BenchmarkLine.findAllMatchIn(outcome).map(_.group(1))
    val details = BenchmarkLine.split(outcome)
    val means = details.flatMap(_.split("\n")).filter(_.trim.startsWith("mean"))
    val doubles = means.map(convertToNS)
    names.toSeq.zip(doubles)
  }

  private def convertToNS(meanLine: String) = {
    val double = matchMean.findFirstIn(meanLine).get.toDouble
    val factor = meanLine match {
      case m if m.contains("ns") => 1
      case m if m.contains("μs") => 1000
      case m if m.contains("ms") => 1000 * 1000
      case m if m.contains(" s") => 1000 * 1000 * 1000
    }
    double * factor
  }

  def calculateScore(deltas: ArrayBuffer[(String, Double)]) = {
    var score = 100
    val annotations = new ArrayBuffer[Error]
    for ((n, v) <- deltas) {
      val diff = Math.abs(v).round
      if (diff > 50) {
        if (score > 0) {
          score -= (diff / 8).toInt
        }
        val (line, file) = FunctionMap.getOrElse(n, (0, studentAnswer.getName))
        annotations.append(new Error(s"Function $n is inefficient -> $diff ns diff!",
          file, line, 0, "complexity"))
      }
    }
    (annotations, Math.max(score, 0))
  }

  private class Caller(command: String) extends Callable[(String, Int)] {
    override def call(): (String, Int) = {
      val lines = new ArrayBuffer[String]
      val exitStatus = command ! ProcessLogger(line => lines.append(line))

      (lines.mkString("\n"), exitStatus)
    }
  }

}
