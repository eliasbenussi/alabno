package json_parser

import java.io.File

import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

/**
  * Tests for MicroServiceInput
  */
class MicroServiceInputTest extends FlatSpec {
  behavior of "MicroServiceInputParser"

  it should "Create a valid JSON file" in {
    val f = new File("test.json")
    MicroServiceInputParser.writeFile(f, "test", "test", Seq().asJava, "test")
    assert(f.exists() && f.isFile)
    assert(f.delete())
  }

  it should "Create a valid JSON file that is equal to its representation" in {
    val f = new File("test.json")
    val msi = MicroServiceInputParser.writeFile(f, "test", "test", Seq().asJava, "test")
    assert(f.exists() && f.isFile)
    val newMsi = MicroServiceInputParser.parseFile(f)
    assert(msi.equals(newMsi))
    assert(f.delete())
  }

}