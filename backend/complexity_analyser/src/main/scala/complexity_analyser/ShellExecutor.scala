package complexity_analyser

import java.util.concurrent.Callable

import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

class ShellExecutor(command: String) extends Callable[String] {
  override def call(): (String) = {
    val lines = new ArrayBuffer[String]
    val exitStatus = command ! ProcessLogger(line => lines.append(line))
    if (exitStatus != 0) throw new Exception(s"$command failed to run: $lines")
    lines.mkString("\n")
  }
}
