package compile

import java.io.File

import scala.collection.mutable.ArrayBuffer
import scala.sys.process._
import json_parser.Error

trait Parser {

  protected val digitsMatch = "([0-9])+".r
  protected val reasonMatch = ":(.*?)".r

  protected def removeOldFiles(path: File, fileType: String) = {
    val compileFiles = path.listFiles().filter(e
    => e.getName.endsWith(fileType)).mkString(" ")
    if (!compileFiles.isEmpty) {
      s"rm $compileFiles" !
    }
  }
  def process(lines: ArrayBuffer[String]): Seq[Error] = Seq()
  def check(file: File): (Int, Seq[Error]) = (0, Seq())
}
