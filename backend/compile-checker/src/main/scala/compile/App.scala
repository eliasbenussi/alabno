package compile

import java.io.{File, FileInputStream}

import play.api.libs.json.Json
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import scala.collection.mutable.ArrayBuffer
import sys.process._

/**
  * This program uses JSON as both input and output.
  * <p> We return a score of 100 if the
  * <p> program compiles, or a 0 otherwise, with the
  * <p> corresponding errors associated to the compilation.
  * <p> Expected input format: <p>
  * <p> { </p>
  * <p>   "input_directory": "<path to a file>", </p>
  * <p>   "type": "<language to be used>", </p>
  * <p> } </p>
 */
object App {

  private var path: File = _

  def main(args : Array[String]) {
    if (args.length != 1) {
      throw new IllegalArgumentException("Compile checker <input json> ")
    }


    val inputReader = new FileInputStream(args.apply(0))
    val json = Json.parse(inputReader)
    path = new File((json \ "input_directory").as[String])
    val mLanguage = (json \ "type").asOpt[String]
    val language = mLanguage.getOrElse("haskell")
    compileCheck(language, path).toString.isEmpty)
  }

  private def checkHaskell(path: File) = {
    val haskellFiles = path.listFiles().filter(e
        => e.getName.endsWith(".hs") || e.getName.endsWith(".lhs")).mkString(" ")
    val lines = new ArrayBuffer[String]()
    val exit = s"ghc -i$path/IC -i$path $haskellFiles" ! ProcessLogger(line => lines.append(line))
    println(exit)
    lines.tail.foreach(println)
    0
  }

  private def checkJava(path: File) = {
    val javaFiles = 
      path.listFiles().filter(e => e.getName.endsWith(".java")).mkString(" ")
    s"javac $javaFiles" !
  }

  private def compileCheck(language: String, path: File): Int = language match {
    case "haskell" => checkHaskell(path)
    case "java" => checkJava(path)
    case _ => throw new NotImplementedException
  }
}
