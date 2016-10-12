package linter.linters

import java.io.File

import linter.Language

/**
  * Example class to showcase inheritance
  * @param file The file(s) to be checked - Can be a directory
  * @param language The language of the file(s)
  */
class CustomLinter(file: File, language: Language.Value) extends BaseLinter(file, language) {

}
