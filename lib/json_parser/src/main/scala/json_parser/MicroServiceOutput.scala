package json_parser

import java.io.{File, FileInputStream, FileWriter}

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.collection.JavaConverters._

class MicroServiceOutput(score: Double, annotations: java.util.List[Error], errors: java.util.List[String]) {

  private implicit val errorWrites = new Writes[Error] {
    def writes(error: Error) = Json.obj(
      "errortype" -> error.getType,
      "filename" -> error.getFile,
      "lineNo" -> error.getLineNo,
      "charNo" -> error.getColNo,
      "text" -> error.getMsg
    )
  }

  def getScore = score

  def getAnnotations = annotations

  def getErrors = errors

  def writeFile(file: File): Unit = {
    val outputStream = new FileWriter(file)
    val result = Json.obj(
      "score" -> score,
      "annotations" -> Json.toJson(annotations.asScala),
      "errors" -> errors.asScala
    )
    outputStream.write(Json.stringify(result))
    outputStream.close()
  }
}

/**
  * Use this object to generate MicroServiceOutputParser, as MicroServiceOutput may become private
  */
object MicroServiceOutputParser {

  private implicit val errorRead: Reads[Error] = (
    (JsPath \ "text").read[String] and
      (JsPath \ "filename").read[String] and
      (JsPath \ "lineNo").read[Int] and
      (JsPath \ "charNo").read[Int] and
      (JsPath \ "errortype").read[String]
    ) (Error.apply _)

  def parseFile(file: File) = {
    val inputReader = new FileInputStream(file)
    val json = Json.parse(inputReader)
    val score = (json \ "score").as[Double]
    val annotations = (json \ "annotations").validate[List[Error]].asOpt.getOrElse(Seq())
    val errors = (json \ "errors").asOpt[List[String]].getOrElse(Seq())
    new MicroServiceOutput(score, annotations.asJava, errors.asJava)
  }

  def writeFile(file: File, score: Double, annotations: java.util.List[Error], errors: java.util.List[String]): Unit = {
    new MicroServiceOutput(score, annotations, errors).writeFile(file)
  }

}
