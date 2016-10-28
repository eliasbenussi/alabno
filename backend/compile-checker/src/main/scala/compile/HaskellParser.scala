package compile

import java.io.File

import json_parser.Error

import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

object HaskellParser extends Parser {
  private val pathMatch = "(.*.hs)".r
  private val posMatch = s":$digitsMatch:$digitsMatch".r

  private def genAnnotations(error: (String, String)): Error = {
    val errorLine = error._1
    val errorDetails = error._2
    val path = pathMatch.findFirstIn(errorLine).get
    val positions = posMatch.findFirstIn(errorLine).get
    val d = digitsMatch.findAllMatchIn(positions).toArray
    val digits = d.map(_.toString).map(Integer.decode)
    new Error(errorDetails.trim, path, digits.apply(0), digits.apply(1),
      "compilation")
  }

  override def process(lines: ArrayBuffer[String]): Seq[Error] = {
    val errorMatch = s"$pathMatch$posMatch$reasonMatch".r
    val text = lines.mkString("\n")
    val errorDetails = errorMatch.split(text).tail
    val errors = errorMatch.findAllIn(text)
    errors.zip(errorDetails.toIterator).map(e => genAnnotations(e)).toSeq
  }

  override def check(path: File) = {
    val haskellFiles = path.listFiles().
      filter(e => e.getName.endsWith(".hs")).mkString(" ")
    removeOldFiles(path, ".hi")
    val lines = new ArrayBuffer[String]()
    val exit = s"ghc -i$path/IC -i$path $haskellFiles" !
      ProcessLogger(line => lines.append(line))
    (exit, process(lines))
  }
}
