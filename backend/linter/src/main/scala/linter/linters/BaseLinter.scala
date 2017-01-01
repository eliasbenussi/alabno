package linter.linters

import java.io.File
import java.nio.file.NoSuchFileException
import json_parser.Error
import linter.Language

/**
  * Abstract class to be inherited by other linters
  *
  * @param file     The file to be used in the linter, can be a directory
  * @param language Language to be used in the linter
  */
abstract class BaseLinter(file: File, language: Language.Value) {
  /*
   * fileList is protected lazy so that other linters may use it if needed
   */
  protected lazy val fileList = findFiles
  private final val regex = """\w*Bench|Test\w+""".r
  /**
    * <p> Inheriting classes should override this method </p>
    * <p> This method should run an operation on every file given and produce a list of mistakes </p>
    *
    * @return The list of mistakes found in the file(s)
    */
  def parseFiles: Seq[Error] = Seq()

  private def recursiveListFiles(f: File): Seq[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }


  private def skipBenchAndTests(f: File) = regex.findFirstIn(f.getName).isDefined

  private def findFiles = {
    if (!file.exists) throw new NoSuchFileException(s"File $file does not exist")
    if (file.isDirectory) {
      recursiveListFiles(file).filter(f => Language.matchExtension(f.getName, language)).filterNot(skipBenchAndTests)
    } else {
      Seq(file)
    }
  }
}
