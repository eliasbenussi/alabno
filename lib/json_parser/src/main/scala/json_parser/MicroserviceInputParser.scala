package json_parser

import java.io.{File, FileInputStream, FileWriter}

import play.api.libs.json.{JsValue, Json, Writes}

import scala.collection.JavaConverters._

/**
  * Created by helicopter88 on 23/10/16.
  */
class MicroserviceInput(path: String, language: String, list: java.util.List[String]) extends Writes[MicroserviceInput] {


  def printToFile(file: File): Unit = {
    val outputStream = new FileWriter(file)
    outputStream.write(Json.stringify(writes(this)))
  }

  override def writes(o: MicroserviceInput): JsValue = Json.obj(
    "input_directory" -> path,
    "type" -> language,
    "additional_config" -> list.asScala
  )
}

object MicroserviceInputParser {

  def parseFile(file: File) = {
    val inputReader = new FileInputStream(file)
    val json = Json.parse(inputReader)
    val path = (json \ "input_directory").as[String]
    val mLanguage = (json \ "type").asOpt[String]
    val language = mLanguage.getOrElse("haskell")
    val list = (json \ "additional_config").asOpt[List[String]].getOrElse(Seq())
    new MicroserviceInput(path, language, list.asJava)
  }

  def writeFile(file: File, path: String, language: String, config: java.util.List[String]) = {
    new MicroserviceInput(path, language, config).printToFile(file)
  }
}