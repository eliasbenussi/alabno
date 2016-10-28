package compile

import java.io.File
import json_parser.Error
import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

/**
  * Object used to compile java files and parse eventual errors
  */
object JavaParser extends Parser {
  private val pathMatch = "(.*?.java)".r
  private val posMatch = s":$digitsMatch".r

  // TODO: deal with java errors
  override def process(lines: ArrayBuffer[String]): Seq[Error] = {
    lines.foreach(println)
    Seq()
  }


  override def check(path: File) = {
    val javaFiles =
      path.listFiles().filter(e => e.getName.endsWith(".java")).mkString(" ")
    val lines = new ArrayBuffer[String]()
    val exit = s"javac $javaFiles" ! ProcessLogger(line => lines.append(line))

    (exit, process(lines))
  }
}
