package linter

import java.io.File

import json_parser.{Error, MicroServiceOutputParser, MicroServiceInputParser}
import linter.linters.{BaseLinter, ExternalLinter, LengthCheckerLinter}

import scala.collection.JavaConverters._
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
  private val mistakes = new mutable.MutableList[Error]
  private val executionErrors = new mutable.MutableList[String]
  private var language: Language.Value = Language.Other
  private var path: File = _

  /**
    * Entry point of this service
    *
    * @param args
    * <p> Array of arguments, where the first argument is the input file </p>
    * <p>  and the second one is the output file </p>
    */
  def main(args: Array[String]) {
    if (args.length != 2) {
      throw new IllegalArgumentException(s"linter <input json> <output json>")
    }
    val inputJSON = MicroServiceInputParser.parseFile(new File(args.apply(0)))

    path = new File(inputJSON.getPath)
    language = Language.matchString(inputJSON.getLanguage)
    var list = inputJSON.getList.asScala.toSeq
    if(list.length == 0)
        list = Seq("baselinter", "external_linter")
    try {
      list.foreach(parseLinters)
      mistakes ++= lintersList.flatMap(_.parseFiles)
    } catch {
      case e: Exception => executionErrors += e.getMessage
    }

    MicroServiceOutputParser.writeFile(new File(args.apply(1)), OutputGenerator.getScore, mistakes.asJava, executionErrors.asJava)
  }

  private def parseLinters(str: String): Unit = str match {
    case "baselinter" => lintersList += new LengthCheckerLinter(path, language)
    case "external_linter" => lintersList += new ExternalLinter(path, language)
    case _ => executionErrors += s"No such linter <$str>"
  }


}
