package linter.linters

import java.io.File

import linter.{Language, LinterError}

import scala.collection.mutable

/**
  * Created by helicopter88 on 12/10/16.
  */
class LengthCheckerLinter(path: File, language: Language.Value) extends BaseLinter(path, language) {
  override def parseFiles: mutable.MutableList[LinterError] = super.parseFiles
}
