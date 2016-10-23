package json_parser

import java.io.{File, FileInputStream, FileWriter}

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.collection.JavaConverters._

/**
  * Created by helicopter88 on 23/10/16.
  */
class MicroserviceOutput(score: Double, annotations: java.util.List[Error], errors: java.util.List[String]) {
  implicit val locationWrites = new Writes[Error] {
    def writes(error: Error) = Json.obj(
      "errortype" -> error._type,
      "filename" -> error._file,
      "lineNo" -> error._lineNo,
      "charNo" -> error._colNo,
      "text" -> error._msg
    )
  }

  def writeFile(file: File): Unit = {
    val outputStream = new FileWriter(file)
    val result = Json.obj(
      "score" -> (100 - score),
      "annotations" -> Json.toJson(annotations.asScala),
      "errors" -> errors.asScala
    )
    outputStream.write(Json.stringify(result))
  }
  
}

object MicroserviceOutputParser {

  implicit val locationRead: Reads[Error] = (
    (JsPath \ "text").read[String] and
      (JsPath \ "filename").read[String] and
      (JsPath \ "lineNo").read[Int] and
      (JsPath \ "colNo").read[Int] and
      (JsPath \ "text").read[String]
    ) (Error.apply _)

  def parseFile(file: File) = {
    val inputReader = new FileInputStream(file)
    val json = Json.parse(inputReader)
    val score = (json \ "score").as[Double]
    val annotations = (json \ "annotations").validate[List[Error]].asOpt
    val errors = (json \ "errors").asOpt[List[String]].getOrElse(Seq())
    new MicroserviceOutput(score, annotations.get.asJava, errors.asJava)
  }

  def writeFile(file: File, score: Double, annotations: java.util.List[Error], errors: java.util.List[String]): Unit = {
    new MicroserviceOutput(score, annotations, errors).writeFile(file)
  }

}

