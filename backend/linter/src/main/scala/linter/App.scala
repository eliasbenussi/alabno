package linter

import java.io.{File, FileInputStream}

import linter.linters.{BaseLinter, ExternalLinter, LengthCheckerLinter}
import play.api.libs.json._

import scala.collection.mutable

/**
  * This program uses JSON both as input and as output
  * <p> Expected input format: <p>
  * <p> { </p>
  * <p>   "input_directory": "<path to a file>", </p>
  * <p>   "type": "<language to be used>", </p>
  * <p>   "additional_config": [ "linter1", "linter2" ] </p>
  * <p> } </p>
  * <p> Language and linters are optional </p>
  *
  * <p> Output format will look like </p>
  * <p> { </p>
  * <p>   "score":   number/10 </p>
  * <p>   "annotations": [ { "lineNo": number, </p>
  * <p>                      "colNo": number, </p>
  * <p>                      "text": "error" </p>
  * <p>                         }], </p>
  * <p>   "errors":  [ ] </p>
  * <p> } </p>
  */
object App {

  private val lintersList = new mutable.MutableList[BaseLinter]
  private val mistakes = new mutable.MutableList[LinterError]
  private val executionErrors = new mutable.MutableList[String]
  private var language: Language.Value = Language.Other
  private var path: File = _

  /**
    * Entry point of this service
    *
    * @param args
    * <p< Array of arguments, where the first argument is the input file </p>
    * <p>  and the second one is the output file </p>
    */
  def main(args: Array[String]) {
    if (args.length != 2) {
      throw new IllegalArgumentException(s"linter <input json> <output json>")
    }
    val inputReader = new FileInputStream(args.apply(0))
    val json = Json.parse(inputReader)
    path = new File((json \ "input_directory").as[String])
    val mLanguage = (json \ "type").asOpt[String]
    language = Language.matchString(mLanguage.getOrElse("haskell"))
    val list = (json \ "additional_config").asOpt[List[String]].getOrElse(Seq("baselinter"))
    try {
      list.foreach(parseLinters)
      mistakes ++= lintersList.flatMap(_.parseFiles)
    } catch {
      case e: Exception => executionErrors += e.getMessage
    }
    OutputGenerator.generateOutput(args.apply(1), mistakes, executionErrors)
  }

  private def parseLinters(str: String): Unit = str match {
    case "baselinter" => lintersList += new LengthCheckerLinter(path, language)
    case "external_linter" => lintersList += new ExternalLinter(path, language)
    case _ => executionErrors += s"No such linter <$str>"
  }


}
