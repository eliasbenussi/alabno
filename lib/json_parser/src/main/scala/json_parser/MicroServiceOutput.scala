package json_parser

import java.io.{File, FileInputStream, FileWriter}

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.collection.JavaConverters._

class MicroServiceOutput(score: Double, annotations: java.util.List[Error],
                         errors: java.util.List[String], additional:
                         Seq[String] = Seq()) {

  private implicit val errorWrites = new Writes[Error] {
    def writes(error: Error) = Json.obj(
      "errortype" -> error.getType,
      "filename" -> error.getFile,
      "lineno" -> error.getLineNo,
      "charno" -> error.getColNo,
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
      "errors" -> errors.asScala,
      "additional_info" -> additional
    )
    outputStream.write(Json.stringify(result))
    outputStream.close()
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case obj: MicroServiceOutput =>
      obj.getScore.equals(score) &&
        obj.getAnnotations.equals(annotations) &&
        obj.getErrors.equals(errors)
    case _ => false
  }

  override def hashCode(): Int = {
    val tmp = ((score.hashCode << 7) + annotations.hashCode) << 31
    tmp + errors.hashCode() << 7
  }
}

/**
  * Use this object to generate MicroServiceOutputParser, as MicroServiceOutput may become private
  */
object MicroServiceOutputParser {

  private implicit val errorRead: Reads[Error] = (
    (JsPath \ "text").readNullable[String] and
      (JsPath \ "filename").readNullable[String] and
      (JsPath \ "lineno").readNullable[Int] and
      (JsPath \ "charno").readNullable[Int] and
      (JsPath \ "errortype").readNullable[String]
    ) (Error.apply _)

  /**
    * Parses a JSON file to produce a MicroServiceOutput instance
    *
    * @param file the file to be parsed
    * @return an instance of MicroServiceOutput containing all the data parsed from f
    */
  def parseFile(file: File) = {
    val inputReader = new FileInputStream(file)
    val json = Json.parse(inputReader)
    val score = (json \ "score").as[Double]
    val annotations = (json \ "annotations").validate[List[Error]].asOpt.getOrElse(Seq())
    val errors = (json \ "errors").asOpt[List[String]].getOrElse(Seq())
    new MicroServiceOutput(score, annotations.asJava, errors.asJava)
  }


  /**
    * Writes a MicroServiceOutput instance to a file as JSON
    *
    * @param file               file to be written to
    * @param microServiceOutput instance to be used when writing
    */
  def writeFile(file: File, microServiceOutput: MicroServiceOutput) = microServiceOutput.writeFile(file)

  /**
    * Writes to a file using the specification written in the documentation
    *
    * @param file        file to be written to
    * @param score       score of the MicroService
    * @param annotations list of annotations of the MicroService
    * @param errors      list of execution errors of the MicroService
    * @param additional  list of res files (first one for model, second for
    *                    student)
    * @return the instance of MicroServiceOutput used to write to the file
    */
  def writeFile(file: File, score: Double, annotations: java.util
  .List[Error], errors: java.util.List[String], additional: Seq[String] = Seq())
  = {
    val microServiceOutput: MicroServiceOutput = new MicroServiceOutput(score, annotations, errors, additional)
    microServiceOutput.writeFile(file)
    microServiceOutput
  }

}
