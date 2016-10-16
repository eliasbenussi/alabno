package linter

import java.io.FileWriter

import play.api.libs.json.{Json, Writes}

/**
  * Generates output as JSON and prints it to a file from the given input
  *
  * @param output          The name of the file to be written
  * @param mistakes        List of mistakes to be written in the file
  * @param executionErrors List of errors encountered during the execution
  */
private class OutputGenerator(output: String, mistakes: Seq[LinterError], executionErrors: Seq[String]) {

  /*
   * Needed by JSON to properly format LinterError
   * Since this has to be implicit,
   * it has to be stored in the class that wants to write the JSON
   */
  implicit val locationWrites = new Writes[LinterError] {
    def writes(linterError: LinterError) = Json.obj(
      "lineNo" -> linterError._lineNo,
      "charNo" -> linterError._colNo,
      "text" -> linterError._msg
    )
  }

  private val outputStream = new FileWriter(output)
  private val result = Json.obj(
    "score" -> calculateScore,
    "annotations" -> Json.toJson(mistakes),
    "errors" -> executionErrors
  )

  // We are very harsh and we take mistakes very seriously
  private def calculateScore = if (mistakes.length >= 10) 0 else 10 - mistakes.length

  outputStream.write(Json.stringify(result))
  outputStream.close()
}

/*
 * Companion class used to create instances of OutputGenerator
 */
object OutputGenerator {
  def generateOutput(output: String, mistakes: Seq[LinterError], executionErrors: Seq[String]): Unit = {
    new OutputGenerator(output, mistakes, executionErrors)
  }
}
