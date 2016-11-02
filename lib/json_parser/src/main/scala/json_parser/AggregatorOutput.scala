package json_parser

import java.io.{File, FileWriter}

import play.api.libs.json._

import scala.collection.JavaConverters._


/**
  * Class used to represent the AggregatorOutput, use the AggregatorOutputParser to create instances of this class
  */
class AggregatorOutput(annotations: java.util.List[Error], letterScore: String, numberScore: Double) {

  private implicit val errorWrites = new Writes[Error] {
    def writes(error: Error) = Json.obj(
      "errortype" -> error.getType,
      "filename" -> error.getFile,
      "lineno" -> error.getLineNo,
      "charno" -> error.getColNo,
      "text" -> error.getMsg
    )
  }

  def printToFile(file: File): Unit = {
    val outputStream = new FileWriter(file)
    val res = Json.obj(
      "letter_score" -> letterScore,
      "number_score" -> numberScore,
      "annotations" -> annotations.asScala)

    outputStream.write(Json.stringify(res))
    outputStream.close()
  }

}

/**
  * Use this object to generate AggregatorOutput
  */
object AggregatorOutputParser {
  def writeFile(file: File, annotations: java.util.List[Error], letterScore: String, numberScore: Double) = {
    new AggregatorOutput(annotations, letterScore, numberScore).printToFile(file)
  }
}
