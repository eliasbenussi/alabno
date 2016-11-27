package mark_marker.trainer

import java.io.{File, FileInputStream, FileOutputStream, FileWriter}

import edu.stanford.nlp.classify.ColumnDataClassifier
import mark_marker.{App, SainsburyException, Utils}

import scala.io.Source

object InitialTraining {

  private def parse(string: String) = {
    string.split(":")(1).trim.replace("}", "")
  }

  // Generates a training set from a path
  // Args[0] is the input path
  // Args[1] is the extension of the file
  def main(args: Array[String]): Unit = {
    if (args.length != 2) throw new IllegalArgumentException("Not enough arguments")

    val db = new DatabaseConnector
    db.connect()
    val t = args(1)
    val inputFolder = args(0)

    val tData = generateTrainingFromPath(new File(inputFolder), Utils.matchType(t))
    val prop = new File("mark_marker/hs_basic_training.prop").getPath

    val cdc = new ColumnDataClassifier(prop)
    val trainFile = s"$t.train"
    val fos = new FileWriter(trainFile)
    fos.write(tData)
    fos.close()
    val cl = cdc.makeClassifier(cdc.readTrainingExamples(trainFile))
    db.addTrainingAndCdc(t, tData, cl)
    db.close()
  }

  private def analyseFolder(file: File, extension: String) = {
    if (file.isFile) throw new SainsburyException
    val grade = file.listFiles().find(_.getName == "grade").get
    val f = Utils.getFiles(file, extension)
    val text = Utils.stringifyFile(f)
    val g = parse(Source.fromFile(grade).mkString)
    (g.trim.toUpperCase, text)
  }



  private def generateTrainingFromPath(path: File, t: String) = {
    if (!path.isDirectory) throw new Exception(s"Directory expected $path")
    val trainingData = path.listFiles().map(analyseFolder(_, t))
    val sb = new StringBuilder
    trainingData.foreach(g => sb.append(String.format("%s\t%s\n", g._1, g._2)))
    sb.toString()
  }
}
