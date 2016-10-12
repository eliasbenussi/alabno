package linter
import java.io.File

import linter.linters.{BaseLinter, LengthCheckerLinter}
import play.api.libs.json._

import scala.collection.mutable
/**
 * @author ${user.name}
 */
object App {

  private val lintersList = new mutable.MutableList[BaseLinter]
  private var language: Language.Value = Language.Other
  private var path: File = _
  private val mistakes = new mutable.MutableList[LinterError]
  def main(args : Array[String]) {
    val json = Json.parse(System.in)
    path = new File((json \ "path").as[String])
    language = Language.matchString((json \ "language").as[String])
    val list = (json \ "linters").asOpt[List[String]].get
    list.foreach(parseLinters)
    mistakes ++= lintersList.flatMap(_.parseFiles)
    print("mistakes" -> Json.toJson(mistakes.map(_.toString)))
    //linters.toArray().foreach(l => parseLinters(l.toString))

    //val linter = new Linter(new File(path), Language.matchString(language))
    //val out = Json.toJson("mistakes" -> )
    //print(linter.parseFiles.toList)
  }

  private def parseLinters(str: String): Unit = str match {
    case "baselinter" => lintersList += new LengthCheckerLinter(path, language)
    case _ => throw new NotImplementedError(s"Wrong linter $str")
  }


}
