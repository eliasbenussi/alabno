package json_parser

import java.io.File

import org.scalatest.FlatSpec
import scala.collection.JavaConverters._

/**
  * Tests for MicroServiceOutput
  */
class MicroServiceOutputTest extends FlatSpec {

  behavior of "MicroServiceOutputTest"

  it should "write a valid JSON filee" in {
    val annotations = Seq(new Error("test", "test", 0, 0, "test"))
    val f = new File("test.json")
    val microServiceOutput = MicroServiceOutputParser.writeFile(f, 100.0d, annotations.asJava, Seq().asJava)
    assert(f.exists() && f.isFile)
    assert(f.delete())
  }

  it should "Create a JSON file that is equal to its representation" in {
    val annotations = Seq(new Error("test", "test", 0, 0, "test"))
    val f = new File("test.json")
    val microServiceOutput = MicroServiceOutputParser.writeFile(f, 100.0d, annotations.asJava, Seq().asJava)
    assert(f.exists() && f.isFile)
    val microServiceOutputNew = MicroServiceOutputParser.parseFile(f)
    assert(microServiceOutputNew.equals(microServiceOutput))
    assert(f.delete())
  }

}
