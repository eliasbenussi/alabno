package compile

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

class ParserTest extends FlatSpec with Matchers {

  behavior of "HaskellParser"

  it should "should return 0 for compilable haskell file" in {
    val hCheck = HaskellParser.check(new File("testFiles/HaskellCorrect"))
    assert(hCheck._1 == 0)
    assert(hCheck._2.isEmpty)
  }

  it should "should not return 0 for compilable haskell file" in {
    val hCheck = HaskellParser.check(new File("testFiles/HaskellIncorrect"))
    assert(hCheck._1 != 0)
    assert(hCheck._2.head.toString.contains("The type signature for " +
      "‘nextPrime’ lacks an accompanying binding"))
  }

  behavior of "JavaParser"

  it should "should return 0 for compilable java file" in {
    val jCheck = JavaParser.check(new File("testFiles/JavaCorrect"))
    assert(jCheck._1 == 0)
    assert(jCheck._2.isEmpty)
  }

  it should "should not return 0 for compilable java file" in {
    val jCheck = JavaParser.check(new File("testFiles/JavaIncorrect"))
    assert(jCheck._1 != 0)
    assert(jCheck._2.head.toString.contains("error: ';' expected"))
  }

}
