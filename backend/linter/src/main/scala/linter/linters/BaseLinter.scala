package linter.linters

import java.io.File
import java.nio.file.NoSuchFileException

import linter.{Language, LinterError}

import scala.collection.mutable
import scala.io.Source

/**
  * Created by helicopter88 on 11/10/16.
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

  def parseFiles = {
    fileList.filterNot(_.isDirectory).foreach(scanFile)
    mistakes
  }
}
