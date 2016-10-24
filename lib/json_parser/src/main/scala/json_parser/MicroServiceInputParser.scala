package json_parser

import java.io.{File, FileInputStream, FileWriter}

import play.api.libs.json.Json

import scala.collection.JavaConverters._

class MicroServiceInput(path: String, language: String, list: java.util.List[String]) {

  def getPath = path

  def getLanguage = language

  def getList = list

  def printToFile(file: File): Unit = {
    val outputStream = new FileWriter(file)
    val res = Json.obj(
      "input_directory" -> path,
      "type" -> language,
      "additional_config" -> list.asScala
    )
    outputStream.write(Json.stringify(res))
    outputStream.close()
  }

}

/**
  * Use this object to generate a MicroServiceInput, which may become private at one point
  */
object MicroServiceInputParser {

  /**
    * Creates a new MicroServiceInput from a JSON file
    *
    * @param file the JSON file to be parsed
    * @return an instance of MicroServiceInput
    */
  def parseFile(file: File) = {
    val inputReader = new FileInputStream(file)
    val json = Json.parse(inputReader)
    val path = (json \ "input_directory").as[String]
    val language = (json \ "type").asOpt[String].getOrElse("haskell")
    val list = (json \ "additional_config").asOpt[List[String]].getOrElse(Seq())
    new MicroServiceInput(path, language, list.asJava)
  }

  /**
    * Writes to a file in JSON using the format specified by the documentation
    *
    * @param file     file to write to
    * @param path     path used by the MicroService
    * @param language Language used by the MicroService
    * @param config   Additional config for the MicroService
    */
  def writeFile(file: File, path: String, language: String, config: java.util.List[String]) = {
    new MicroServiceInput(path, language, config).printToFile(file)
  }
}