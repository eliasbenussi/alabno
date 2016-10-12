package linter.linters

import java.io.File

import linter.{Language, LinterError}

import scala.collection.mutable

/**
  * A linter that checks if a line goes over 80 characters
  * <p> Inherits everything from BaseLinter </p>
  */
class LengthCheckerLinter(path: File, language: Language.Value) extends BaseLinter(path, language) {
  /**
    * For every file given, checks whether any line is bigger than 80 characters
    * @return The list of mistakes found in the file(s)
    */
  override def parseFiles: mutable.MutableList[LinterError] = super.parseFiles
}
