package linter.linters

import java.io.File
import java.nio.file.NoSuchFileException

import linter.{Language, LinterError}

import scala.collection.mutable
import scala.io.Source

/**
  * Abstract class to be inherited by other linters
  * @param file The file to be used in the linter, can be a directory
  * @param language Language to be used in the linter
  */
abstract class BaseLinter(file: File, language: Language.Value) {
  private val mistakes = new mutable.MutableList[LinterError]
  private val fileList = findFiles

  private def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  private def findFiles = {
    if (!file.exists) throw new NoSuchFileException(s"File $file does not exist")
    if (file.isDirectory) {
      recursiveListFiles(file).filter(f => Language.matchExtension(f.getName, language))
    } else {
      Array(file)
    }
  }

  private def scanFile(f: File): Unit = {
    for((line,index) <- Source.fromFile(f).getLines().zipWithIndex) {
      if(line.length >= 80) {
        mistakes += new LinterError("Line is over 80 characters", f.toString, index + 1)
      }
    }
  }

  /**
    * <p> Inheriting classes should override this method </p>
    * <p> This method should run an operation on every file given and produce a list of mistakes </p>
    * @return The list of mistakes found in the file(s)
    */
  def parseFiles = {
    fileList.filterNot(_.isDirectory).foreach(scanFile)
    mistakes
  }
}
