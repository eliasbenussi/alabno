package mark_marker

import java.io.File

import edu.stanford.nlp.classify.{Classifier, ColumnDataClassifier}
import json_parser.{MicroServiceInputParser, MicroServiceOutputParser}
import mark_marker.trainer.DatabaseConnector

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

object App {

  def main(args: Array[String]): Unit = {
    if (args.length != 2)
      throw new IllegalArgumentException("Not enough arguments")
    val mi = MicroServiceInputParser.parseFile(new File(args(0)))
    val errors = new ArrayBuffer[String]()
    var score = 0.0d
    try {
      val files = Utils.getFiles(new File(mi.getPath), Utils.matchType(mi.getLanguage))
      val text = Utils.stringifyFile(files)

      val (cdc, classifier) = generateCdc(mi.getLanguage)
      val d = cdc.makeDatumFromLine("0\t" + text)

      val grade = classifier.classOf(d).trim
      score = matchScore(grade)
    } catch {
      case e: Throwable => errors += e.toString
    }
    MicroServiceOutputParser.writeFile(new File(args(1)), score, Seq().asJava, errors.asJava)
  }

  private def generateCdc(t: String): (ColumnDataClassifier, Classifier[String, String]) = {
    val prop = new File("mark_marker/hs_basic_training.prop").getPath
    val cdc = new ColumnDataClassifier(prop)
    val classifier = getClassifier(t)
    (cdc, classifier)
  }

  private def getClassifier(exercise: String) = {
    val db = new DatabaseConnector
    db.connect()
    val classifier = db.getSerialisedClassifier(exercise)
    db.close()
    if (classifier == null) throw new IllegalArgumentException(s"$exercise is not a valid type")
    classifier
  }

  private def matchScore(score: String) = score match {
    case "A*" => 95
    case "A+" => 85
    case "A" => 75
    case "B" => 65
    case "C" => 55
    case "D" => 45
    case "E" => 35
    case "F" => 25
    case _ => 0
  }

}
