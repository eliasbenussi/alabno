package compile

import java.io.File
import json_parser.{Error, MicroServiceInputParser, MicroServiceOutputParser}
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

/**
  * This program uses JSON as both input and output.
  * <p> We return a score of 100 if the </p>
  * <p> program compiles, or a 0 otherwise, with the </p>
  * <p> corresponding errors associated to the compilation. </p>
  * <p> Expected input format: <p>
  * <p> { </p>
  * <p>   "input_directory": "<path to a file>", </p>
  * <p>   "type": "<language to be used>", </p>
  * <p> } </p>
 */
object App {

  private var path: File = _
  private var score: Int = 100
  val annotations = new ArrayBuffer[Error]
  val errorList = new ArrayBuffer[String]

  val digitsMatch = "([0-9])+".r
  val reasonMatch = ":(.*?)".r

  def main(args : Array[String]) {
    if (args.length != 2) {
      throw new IllegalArgumentException("Compile checker <input json> ")
    }
    val parser = MicroServiceInputParser.parseFile(new File(args.apply(0)))

    path = new File(parser.getPath)
    val language = parser.getLanguage
    if (compileCheck(language, path) != 0) {
      score = 0
    }

    MicroServiceOutputParser.writeFile(new File(args.apply(1)), score,
      annotations.asJava, errorList.asJava)
  }

  //The 2 process functions can be generalised maybe
  // They are meant to put the errors in annotations

  def haskellProcess(lines: ArrayBuffer[String]) = {
    val pathMatch = "(.*?.hs)".r
    val posMatch = s":$digitsMatch:$digitsMatch".r

    var error = s"$pathMatch$posMatch$reasonMatch".r

    val errors = error.split(lines.mkString("\n")).tail

    /*for(line <- lines) {
      if(line.matches(s"$pathMatch$posMatch$reasonMatch")){
        path = pathMatch.findFirstIn(line).get
        println(path)
        pathMatch.
      }
    }*/
    errors.foreach(println)
  }

  def javaProcess(lines: ArrayBuffer[String]) = {
    val pathMatch = "(.*?.java)".r
    val posMatch = s":$digitsMatch".r

    for(line <- lines) {
      println(line)
    }
  }

  def removeOldFiles(path: File, fileType: String) = {
    val compileFiles = path.listFiles().filter(e
    => e.getName.endsWith(fileType)).mkString(" ")
    if (!compileFiles.isEmpty) {
      s"rm $compileFiles".!
    }
  }

  private def checkHaskell(path: File) = {
    val haskellFiles = path.listFiles().filter(e
        => e.getName.endsWith(".hs")).mkString(" ")
    removeOldFiles(path, ".hi")
    val lines = new ArrayBuffer[String]()
    val exit = s"ghc -i$path/IC -i$path $haskellFiles" ! ProcessLogger(line
                                                      => lines.append(line))
    haskellProcess(lines)
    exit
  }

  private def checkJava(path: File) = {
    val javaFiles =
      path.listFiles().filter(e => e.getName.endsWith(".java")).mkString(" ")
    val lines = new ArrayBuffer[String]()
    val exit = s"javac $javaFiles" ! ProcessLogger(line => lines.append(line))
    javaProcess(lines)
    exit
  }

  private def compileCheck(language: String, path: File): Int = language match {
    case "haskell" => checkHaskell(path)
    case "java" => checkJava(path)
    case _ => throw new NotImplementedException
  }
}