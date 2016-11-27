package mark_marker

import java.io.File

import edu.stanford.nlp.classify.{Classifier, ColumnDataClassifier}
import mark_marker.trainer.DatabaseConnector

import scala.io.Source

/**
  * Created by eb1314 on 27/11/16.
  */
object Utils {

  def matchType(t: String) = t match {
    case s if s.contains("haskell") => ".hs"
    case s if s.contains("java") => ".java"
    case _ => throw new IllegalArgumentException("Invalid language")
  }

  def getFiles(file: File, extension: String): Array[File] = {
    file.listFiles().filter(_.getName.endsWith(extension))
  }

  def stringifyFile(files: Array[File]): String = {
    files.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
  }
}
