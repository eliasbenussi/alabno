package linter.linters

import java.io.File

import linter.{Language, OutputGenerator}
import json_parser.Error

import scala.sys.process._

/**
  * Example class to showcase inheritance
  *
  * @param file     The file(s) to be checked - Can be a directory
  * @param language The language of the file(s)
  */
class ExternalLinter(file: File, language: Language.Value) extends BaseLinter(file, language) {

  // Keep those lazy so that they are initialised only if we actually need them
  private lazy val digitsMatch = "([0-9])+".r
  private lazy val hLinter = new HLinter
  private lazy val jLinter = new JLinter

  /**
    * This method will call the proper external linter based on the language
    * For Haskell, HLint will be called
    *
    * @return The list of mistakes found in the file(s) by the external linter
    */
  override def parseFiles: Seq[Error] = language match {
    case Language.Haskell => hLinter.check
    case Language.Java => jLinter.check
    case _ => throw new IllegalArgumentException("Wrong language given")
  }

  /*
   * External linters should mixin with this class
   */
  private trait Linter {
    def check: Seq[Error] = Seq()
  }

  /*
   * TODO: implement
   */
  private class JLinter extends Linter {
    private lazy val pathMatch = "(.*?.java)".r
    private lazy val reasonMatch = ":(.*?\n)".r
    private lazy val t = "\\[[A-Z]+\\] ".r
    private final val PATH = "linter/lib"
    private def matchJavaMistake(s: String): Error = {
      // Get rid of the [WARN] bit
      val string = t.replaceFirstIn(s, "")
      val path = pathMatch.findFirstIn(string).get

      val digits = digitsMatch.findAllIn(string).toSeq
      // This is always present
      val lineNo = Integer.decode(digits.head)
      // This one may or may not be present
      var charNo = 0
      if (digits.tail.length >= 0)
        charNo = Integer.decode(digits.apply(1))
      val reason = reasonMatch.findFirstIn(string).get.tail
      OutputGenerator.addScore(0.1d)
      new Error(reason, path, lineNo, charNo, "style")
    }

    override def check: Seq[Error] = {
      // Execute checkstyle from linter/lib with the google style checks
      val checkStyle = s"java -jar $PATH/checkstyle-7.2-all.jar -c $PATH/checkstyle_checks.xml ${file.getPath}" !!
      val fileFinder = s"$t$pathMatch:$digitsMatch(:$digitsMatch)?$reasonMatch".r
      fileFinder.findAllIn(checkStyle).toArray.map(matchJavaMistake)
    }
  }

  /*
   * HLinter: Used to run HLint on haskell files, parse them and return a Seq[LinterError]
   */
  private class HLinter extends Linter {
    private lazy val pathMatch = "(.*?.hs)".r
    private lazy val posMatch = s":$digitsMatch:$digitsMatch".r
    private lazy val reasonMatch = ":(.*?\n)".r

    /*
     * Checks for mistakes using HLint
     */
    override def check: Seq[Error] = {
      val hlint = s"hlint ${file.getPath} --no-exit-code" !!
      val scan = fileList.map(e => s"scan ${e.getPath}" !!).mkString("\n")
      val fileFinder = s"$pathMatch$posMatch$reasonMatch".r
      val hlintErrors = fileFinder.findAllIn(hlint).toArray.map(matchHaskellMistake(_, 1, "semantic"))
      val scanErrors = fileFinder.findAllIn(scan).toArray.map(matchHaskellMistake(_, 0.1, "style"))
      hlintErrors ++ scanErrors
    }

    private def matchHaskellMistake(string: String, value: Double, t: String) = {
      // We can skip the checking because we know this exists
      val path = pathMatch.findFirstIn(string).get
      val positions = posMatch.findFirstIn(string).get
      val d = digitsMatch.findAllMatchIn(positions).toArray
      val digits = d.map(_.toString).map(Integer.decode)
      val reason = reasonMatch.findFirstIn(string).get.tail
      OutputGenerator.addScore(value)
      new Error(reason, path, digits.apply(0), digits.apply(1), t)
    }
  }

}
