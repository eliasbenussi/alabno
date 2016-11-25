package mark_marker.trainer

import java.io.{File, FileWriter}

import scala.io.Source

/**
  * Created by dfm114 on 25/11/16.
  */
object Trainer {

  private def parse(string: String) = {
    string.split(":")(1).trim.replace("}", "")
  }

  // Generates a training set from a path
  // Args[0] is the input path
  // Args[1] is the output path
  // Args[2] is the extension of the file
  def main(args: Array[String]) = {
    if(args.length != 3) throw new IllegalArgumentException("No")

    generateTrainingFromPath(new File(args(0)), new File(args(1)), args(2))
  }

  private def recursiveListFiles(f: File): Seq[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  private def analyseFolder(file: File, extension: String) = {
    if(file.isFile) throw new Exception("No")
    val grade = file.listFiles().find(_.getName == "grade").get
    val f = file.listFiles().filter(_.getName.endsWith(extension))
    val text = f.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
    val g = parse(Source.fromFile(grade).mkString)
    (g.toUpperCase, text)
  }

  private def generateTrainingFromPath(path: File, outputPath: File, t: String) = {
    if(!path.isDirectory) throw new Exception("Hello")
    val trainingData = path.listFiles().map(analyseFolder(_, t))
    val bro = new FileWriter(outputPath)
    trainingData.foreach(g => bro.append(String.format("%s\t%s\n", g._1, g._2)))
  }
}
