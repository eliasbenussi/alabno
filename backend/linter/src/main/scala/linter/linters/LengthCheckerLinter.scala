package linter.linters

import java.io.File

import linter.{Language, LinterError}

import scala.collection.mutable
import scala.io.Source

/**
  * A linter that checks if a line goes over 80 characters
  * <p> Inherits everything from BaseLinter </p>
  */
class LengthCheckerLinter(path: File, language: Language.Value) extends BaseLinter(path, language) {
  private val mistakes = new mutable.MutableList[LinterError]

  /**
    * For every file given, checks whether any line is bigger than 80 characters
    *
    * @return The list of mistakes found in the file(s)
    */
  override def parseFiles: Seq[LinterError] = {
    fileList.filterNot(_.isDirectory).foreach(scanFile)
    mistakes
  }

  private def scanFile(f: File): Unit = {
    for ((line, index) <- Source.fromFile(f).getLines().zipWithIndex) {
      if (line.length >= 80) {
        mistakes += new LinterError("Line is over 80 characters", f.toString, index + 1, 0, 0, "style")
      }
    }
  }
}
