package mark_marker.trainer

import java.io.File

import mark_marker.{SainsburyException, Utils}

import scala.io.Source

object InitialTraining {

  // Generates a training set from a path
  // Args[0] is the input path
  // Args[1] is the extension of the file
  def main(args: Array[String]): Unit = {
    if (args.length != 2) throw new IllegalArgumentException("Not enough arguments")

    val db = new DatabaseConnector("MarkMarkerTest")
    db.connect()
    val t = args(1)
    val inputFolder = args(0)

    val tData = generateTrainingFromPath(new File(inputFolder), Utils.matchType(t))
    val prop = new File("backend/mark_marker/hs_basic_training.prop").getPath
    tData.foreach(db.addTrainingData(t, _))
    // Generate the classifier
    Trainer.getCdc(t, prop, db)
    db.close()
  }

  private def generateTrainingFromPath(path: File, t: String) = {
    if (!path.isDirectory) throw new Exception(s"Directory expected $path")
    val trainingData = path.listFiles().map(analyseFolder(_, t))
    trainingData.map(g => String.format("%s\t%s\n", g._1, g._2))
  }

  private def analyseFolder(file: File, extension: String) = {
    if (file.isFile) throw new SainsburyException
    val grade = file.listFiles().find(_.getName == "grade").get
    val f = Utils.getFiles(file, extension)
    val text = Utils.stringifyFile(f)
    val g = parse(Source.fromFile(grade).mkString)
    (g.trim.toUpperCase, text)
  }

  private def parse(string: String) = {
    string.split(":")(1).trim.replace("}", "")
  }
}
