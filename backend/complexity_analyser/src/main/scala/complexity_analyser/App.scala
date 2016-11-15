package complexity_analyser

import java.io.File

import json_parser.{Error, MicroServiceInputParser, MicroServiceOutputParser}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer


/**
  * Simple main to check the access to resources
  */
object App {
  def main(args: Array[String]): Unit = {
    if (args.length != 2)
      throw new IllegalArgumentException("CompAnal <input.json> <output.json>")
    val mi = MicroServiceInputParser.parseFile(new File(args apply 0))
    val language = mi.getLanguage
    val modelAnswer = new File(mi.getModelAnswer)
    val inputPath = new File(mi.getPath)
    var annotations: Seq[Error] = Seq()
    var errors = new ArrayBuffer[String]
    var score = 100.0d
    try {
      val (a, s) = processLanguage(language, modelAnswer, inputPath)
      annotations = a
      score = s
    } catch {
      case e: Exception => errors += e.getMessage
    }
    MicroServiceOutputParser.writeFile(new File(args apply 1), score,
      annotations.asJava, errors.asJava)
    System.exit(0)
  }

  def processLanguage(language: String, modelAnswer: File, inputPath: File) = {
    language match {
      case "haskell" =>
        val h = new HaskellProcessor(modelAnswer, inputPath)
        h.prepare()
        val (errors, score) = h.runTests()
        val (compErrors, compScore) = h.runBench()
        (errors ++ compErrors, compScore - score)
      case _ => throw new IllegalArgumentException("Wrong language")
    }
  }
}