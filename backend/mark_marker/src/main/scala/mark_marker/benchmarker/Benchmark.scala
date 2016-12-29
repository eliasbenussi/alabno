package mark_marker.benchmarker

import java.io.{File, FileWriter}
import java.util.concurrent.{Callable, Executors}

import mark_marker.trainer.InitialTraining
import mark_marker.{App, Utils}


object Benchmark {

  private lazy val pool = Executors.newFixedThreadPool(4)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) throw new IllegalArgumentException("Not enough arguments")
    val bPath = new File(args(0))
    val exType = args(1)

    val dirs = bPath.listFiles().filter(_.isDirectory)
    val ts = dirs map (d => pool.submit(new BenchmarkRun(exType, d.getAbsolutePath)))
    val values = ts map (_.get())
    val fw = new FileWriter("markMarkBench.log")
    for ((elem, idx) <- values.zipWithIndex) {
      fw.write(String.format("%8s\t%10s\t%10s\n", idx.toString, elem._1.toString, elem._2.toString))
    }

    fw.flush()
    fw.close()
    pool.shutdown()
  }
}

class BenchmarkRun(exType: String, path: String) extends Callable[(Int, Int)] {
  override def call(): (Int, Int) = {
    println(s"hello $path")
    val ext = Utils.matchType(exType)
    val f = new File(path)
    val (grade, text) = InitialTraining.analyseFolder(f, ext)
    val got = App.grade(text = text, language = exType)
    (App.matchScore(grade), got)
  }
}