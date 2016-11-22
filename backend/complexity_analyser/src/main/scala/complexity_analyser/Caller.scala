package complexity_analyser

import java.util.concurrent.Callable

import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

class Caller(command: String) extends Callable[(String, Int)] {
  override def call(): (String, Int) = {
    val lines = new ArrayBuffer[String]
    val exitStatus = command ! ProcessLogger(line => lines.append(line))

    (lines.mkString("\n"), exitStatus)
  }
}
