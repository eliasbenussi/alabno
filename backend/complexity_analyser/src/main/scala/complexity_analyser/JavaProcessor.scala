package complexity_analyser

import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.concurrent.{Callable, ExecutorService, TimeUnit}

import json_parser.Error

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class JavaProcessor(modelAnswer: File, studentAnswer: File, executorService: ExecutorService) {
  private final lazy val beforePattern = """NO (\d+) BEFORE (\w+) => (\d+)""".r
  private final lazy val afterPattern = """NO (\d+) AFTER (\w+) => (\d+)""".r
  private final lazy val jarFile = new File("complexity_analyser/res/javassist.jar")

  private final lazy val TIME_THRESHOLD = 250

  def prepare(): Unit = {
    val benchFile = new File("backend/complexity_analyser/res/Benchmarker.java")
    val modPath = new File(modelAnswer.toString + "/" + benchFile.getName).toPath
    val studPath = new File(studentAnswer.toString + "/" + benchFile.getName).toPath
    copy(benchFile.toPath, modPath, REPLACE_EXISTING)
    copy(benchFile.toPath, studPath, REPLACE_EXISTING)
    compile()
  }

  private def compile() = {
    val modJavaFiles =
      modelAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    val studJavaFiles =
      studentAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    executorService.submit(new ShellExecutor(s"javac -cp $jarFile $studJavaFiles"))
    executorService.submit(new ShellExecutor(s"javac -cp $jarFile $modJavaFiles"))
    executorService.awaitTermination(1, TimeUnit.SECONDS)
  }

  def benchmark() = {
    val testClass = findTestSuite()
    val modThread = executorService.submit(new ShellExecutor(s"java -cp $modelAnswer:$jarFile Benchmarker $testClass"))
    val studThread = executorService.submit(new ShellExecutor(s"java -cp $studentAnswer:$jarFile Benchmarker $testClass"))
    val modMeanT = executorService.submit(new MeanMaker(modThread.get))
    val studMeanT = executorService.submit(new MeanMaker(studThread.get))
    val modMean = modMeanT.get
    val studMean = studMeanT.get
    calculateTestScores(modMean, studMean)
  }

  private def findTestSuite() = {
    val names = modelAnswer.listFiles().map(_.getName)
    names.find(_.endsWith("TestSuite.java")).get.replace(".java", "")
  }

  def calculateTestScores(modMean: Map[String, Long], studMean: Map[String, Long]) = {
    var score = 100.0d
    val annotations = new ArrayBuffer[Error]
    var eff = ""
    for (fun <- modMean) {
      val (testName, modTime) = fun
      // Difference in microseconds
      val diff = (modTime - studMean(testName)) / 1000
      if (Math.abs(diff) > TIME_THRESHOLD) {
        score -= (diff / 100).toInt
        if (diff > 0) {
          eff = s"Function used in test $testName is " +
            s"inefficient -> $diff ms diff!"
        } else {
          eff = s"Function used in test $testName is " +
            s"more efficient than model solution -> $diff ms diff!"
        }
        annotations.append(new Error(eff, studentAnswer.getName, 0, 0, "complexity"))
      }
    }
    (annotations, Math.min(Math.max(score, 0), 100))
  }

  private def findMatch(lines: String) = {
    val map = new mutable.HashMap[String, ArrayBuffer[Long]]
    val split = lines.split("\n")
    val list = new ArrayBuffer[Long]
    for (line <- split) {
      line match {
        case beforePattern(index, name, time) =>
          list.insert(index.toInt, time.toLong)
        case afterPattern(index, name, time) =>
          val diff = time.toLong - list.apply(index.toInt)
          if (!map.contains(name)) {
            map(name) = new ArrayBuffer[Long]
          }
          map(name) += diff
        case _ => None
      }
    }
    map
  }

  private def genMean(diffMap: mutable.HashMap[String, ArrayBuffer[Long]]) = diffMap.mapValues(v => v.sum / v.length)

  private class MeanMaker(input: String) extends Callable[Map[String, Long]] {
    override def call() = {
      val m = findMatch(input)
      genMean(m).toMap
    }
  }
}
