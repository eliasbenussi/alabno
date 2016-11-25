package mark_marker

import java.io.File
import edu.stanford.nlp.classify.ColumnDataClassifier
import json_parser.MicroServiceInputParser

import scala.io.Source
object App {
  def main(args: Array[String]) = {
    if(args.length != 2)
      throw new IllegalArgumentException("Hello, CSG?")
    val mi = MicroServiceInputParser.parseFile(new File(args(0)))
    val files = new File(mi.getPath).listFiles.filter(_.getName.endsWith(".hs"))
    val text = files.flatMap(e => Source.fromFile(e).getLines.mkString("\\n").replace("\t", "\\t")).mkString("")
    val prop = new File("mark_marker/hs_basic_training.prop").getPath
    val trainingSet = new File("training.train").getPath
    val cl = new ColumnDataClassifier(prop)
    val t = cl.readTrainingExamples(trainingSet)

    val d = cl.makeDatumFromLine("\0\t" + text)
    println(cl.classOf(d))
  }
}
