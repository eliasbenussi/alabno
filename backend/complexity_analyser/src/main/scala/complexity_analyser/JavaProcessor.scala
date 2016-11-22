package complexity_analyser

import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.concurrent.Executors

/**
  * Created by dfm114 on 21/11/16.
  */
class JavaProcessor(modelAnswer: File, studentAnswer: File) {
  private final lazy val beforePattern = """NO (\d+) BEFORE (\w+) => (\d+)""".r
  private final lazy val afterPattern = """NO (\d+) AFTER (\w+) => (\d+)""".r
  private final lazy val jarFile = new File("complexity_analyser/res/javassist.jar")
  // Used to run the compilations and the benchmarks
  private final lazy val eS = Executors.newFixedThreadPool(2)

  def prepare(): Unit = {
    val benchFile = new File("complexity_analyser/res/Benchmarker.java")
    val modPath = new File(modelAnswer.toString + "/" + benchFile.getName).toPath
    val studPath = new File(studentAnswer.toString + "/" + benchFile.getName).toPath
    println(benchFile, modPath)
    copy(benchFile.toPath, modPath, REPLACE_EXISTING)
    copy(benchFile.toPath, studPath, REPLACE_EXISTING)
    compile()
  }

  private def compile() = {
    val modJavaFiles =
      modelAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    val studJavaFiles =
      studentAnswer.listFiles().filter(_.getName.endsWith(".java")).mkString(" ")
    val modThread = eS.submit(new Caller(s"javac -cp $jarFile $studJavaFiles"))
    val studThread = eS.submit(new Caller(s"javac -cp $jarFile $modJavaFiles"))
    if (modThread.get._2 != 0) throw new Exception(s"Model solution did not compile: ${modThread.get._1}")
    if (studThread.get._2 != 0) throw new Exception(s"Student solution did not compile: ${studThread.get._1}")
  }

  private def findTestSuite() = {
    val names = modelAnswer.listFiles().map(_.getName)
    names.find(_.endsWith("TestSuite.java")).get.replace(".java", "")
  }

  def findMatch(lines: String) = {
    val split = lines.split("\n")
    for (line <- split) {
      line match {
        case beforePattern(index, name, time) =>
          println(index, name, time)
        case afterPattern(index, name, time) =>
          println("after", index, name, time)
        case _ => None
      }
    }
  }
  def benchmark() = {
    val testClass = findTestSuite()
    val modThread = eS.submit(new Caller(s"java -cp $modelAnswer:$jarFile Benchmarker $testClass"))
    val studThread = eS.submit(new Caller(s"java -cp $studentAnswer:$jarFile Benchmarker $testClass"))
    val (modOut, modExit) = modThread.get
    if (modExit != 0) throw new Exception(s"Model solution did not run: $modOut")
    val (studOut, studExit) = studThread.get
    if (studExit != 0) throw new Exception(s"Student solution did not run: $studOut")
    findMatch(modOut)
    findMatch(studOut)

  }
}
