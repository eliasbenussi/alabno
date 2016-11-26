package mark_marker

import java.io.{File, FileWriter}

import edu.stanford.nlp.classify.ColumnDataClassifier
import json_parser.{MicroServiceInputParser, MicroServiceOutputParser}
import mark_marker.trainer.DatabaseConnector
import scala.collection.JavaConverters._

import scala.io.Source

object App {
  def main(args: Array[String]) = {
    if (args.length != 2)
      throw new IllegalArgumentException("Not enough arguments")
    val mi = MicroServiceInputParser.parseFile(new File(args(0)))
    val files = new File(mi.getPath).listFiles.filter(_.getName.endsWith(matchType(mi.getLanguage)))
    val text = files.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
    val prop = new File("mark_marker/hs_basic_training.prop").getPath
    val trainingSet = getTrainingSet(mi.getLanguage).getPath
    val cl = new ColumnDataClassifier(prop)

    val t = cl.makeClassifier(cl.readTrainingExamples(trainingSet))

    val d = cl.makeDatumFromLine("0\t" + text)

    val grade = t.classOf(d).trim
    MicroServiceOutputParser.writeFile(new File(args(1)), matchScore(grade), Seq().asJava, Seq().asJava)
  }

  def matchScore(score: String) = score match {
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

  def getTrainingSet(exercise: String): File = {
    val f = new File(s"$exercise.train")
    if (f.exists()) return f
    val db = new DatabaseConnector
    db.connect()
    val t = db.getTrainingData(exercise)
    if (t.isEmpty) throw new IllegalArgumentException(s"$exercise is not a valid training set")
    val fw = new FileWriter(f)
    fw.append(t)
    fw.close()
    f
  }

  def matchType(t: String) = t match {
    case s if s.contains("haskell") => ".hs"
    case s if s.contains("java") => ".java"
    case _ => throw new IllegalArgumentException("Invalid language")
  }
}
