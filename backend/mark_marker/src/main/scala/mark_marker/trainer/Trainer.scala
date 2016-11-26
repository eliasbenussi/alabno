package mark_marker.trainer

import java.io.{File, FileWriter}

import mark_marker.{App, SainsburyException}

import scala.io.Source

object Trainer {

  private def parse(string: String) = {
    string.split(":")(1).trim.replace("}", "")
  }

  // Generates a training set from a path
  // Args[0] is the input path
  // Args[1] is the output path
  // Args[2] is the extension of the file
  def main(args: Array[String]): Unit = {
    val db = new DatabaseConnector
    db.connect()
    if(args.length != 3) throw new IllegalArgumentException("Not enough arguments")
    val outputPath: File = new File(args(1))

    val t= args(2)
    val inputFolder = args(0)

    generateTrainingFromPath(new File(inputFolder), outputPath, App.matchType(t))
    db.addTrainingData(t, Source.fromFile(outputPath).getLines.mkString("\n"))
    db.close()
  }

  private def analyseFolder(file: File, extension: String) = {
    if(file.isFile) throw new SainsburyException
    val grade = file.listFiles().find(_.getName == "grade").get
    val f = file.listFiles().filter(_.getName.endsWith(extension))
    val text = f.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
    val g = parse(Source.fromFile(grade).mkString)
    (g.toUpperCase, text)
  }

  private def generateTrainingFromPath(path: File, outputPath: File, t: String) = {
    if(!path.isDirectory) throw new Exception("Directory expected")
    val trainingData = path.listFiles().map(analyseFolder(_, t))
    val bro = new FileWriter(outputPath)
    trainingData.foreach(g => bro.append(String.format("%s\t%s\n", g._1, g._2)))
    bro.close()
  }
}
