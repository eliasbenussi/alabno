package compile

import java.io.File

import json_parser.{Error, MicroServiceInputParser, MicroServiceOutputParser}
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

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

  def main(args: Array[String]) {
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
  
  private def compileCheck(language: String, path: File): Int = language match {
    case s if s.contains("haskell") =>
      val hCheck = HaskellParser.check(path)
      annotations ++= hCheck._2
      hCheck._1

    case s if s.contains("java") =>
      val hCheck = JavaParser.check(path)
      annotations ++= hCheck._2
      hCheck._1

    case _ => throw new NotImplementedException
  }
}