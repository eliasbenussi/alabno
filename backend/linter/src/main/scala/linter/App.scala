package linter

import java.io.{File, FileInputStream, FileWriter}

import linter.linters.{BaseLinter, LengthCheckerLinter}
import play.api.libs.json._

import scala.collection.mutable

/**
  * @author ${user.name}
  */
object App {

  private val lintersList = new mutable.MutableList[BaseLinter]
  private val mistakes = new mutable.MutableList[LinterError]
  private val executionErrors = new mutable.MutableList[String]
  private var language: Language.Value = Language.Other
  private var path: File = _

  def main(args: Array[String]) {
    if (args.length != 2) {
      throw new IllegalArgumentException(s"linter <input json> <output json>")
    }
    val inputReader = new FileInputStream(args.apply(0))
    val json = Json.parse(inputReader)
    path = new File((json \ "path").as[String])
    val mLanguage = (json \ "language").asOpt[String]
    language = Language.matchString(mLanguage.getOrElse("haskell"))
    val list = (json \ "linters").asOpt[List[String]].getOrElse(Seq("baselinter"))
    try {
      list.foreach(parseLinters)
      mistakes ++= lintersList.flatMap(_.parseFiles)
    } catch {
      case e: Exception => executionErrors += e.getMessage
    }
    val outputStream = new FileWriter(args.apply(1))

    val result = Json.obj(
      "mistakes" -> mistakes.map(_.toString),
      "execution_errors" -> executionErrors.map(_.toString)
    )
    outputStream.write(Json.stringify(result))
    outputStream.close()
  }

  private def parseLinters(str: String): Unit = str match {
    case "baselinter" => lintersList += new LengthCheckerLinter(path, language)
    case _ => executionErrors += s"No such linter <$str>"
  }


}
