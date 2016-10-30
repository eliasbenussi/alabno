package compile

import java.io.File
import json_parser.Error
import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

/**
  * Object used to compile java files and parse eventual errors
  */

object JavaParser extends Parser {
  private val pathMatch = "(.*.java)".r
  private val posMatch = s":$digitsMatch".r
  private val javaReasonMatch = ": (.*)".r

  private def genAnnotations(error: String): Error = {
    val path = pathMatch.findFirstIn(error).get
    val positions = posMatch.findFirstIn(error).get
    val row = digitsMatch.findFirstIn(positions).get.toInt
    val errorReason = javaReasonMatch.findFirstIn(error).get.substring(2)
    new Error(errorReason, path, row, 0, "compilation")
  }

  override def process(lines: ArrayBuffer[String]): Seq[Error] = {
    val errorMatch = s"$pathMatch$posMatch$javaReasonMatch".r
    val text = lines.mkString("\n")
    val errors = errorMatch.findAllIn(text)
    errors.map(e => genAnnotations(e)).toSeq
  }

  override def check(path: File) = {
    val javaFiles =
      path.listFiles().filter(e => e.getName.endsWith(".java")).mkString(" ")
    val lines = new ArrayBuffer[String]()
    val exit = s"javac $javaFiles" ! ProcessLogger(line => lines.append(line))
    removeOldFiles(path, ".class")
    (exit, process(lines))
  }
}
