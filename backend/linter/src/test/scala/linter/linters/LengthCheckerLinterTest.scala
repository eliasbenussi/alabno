import java.io.File
import java.nio.file.NoSuchFileException
import linter.Language
import org.scalatest.{FlatSpec, Matchers}
import linter.linters._

class LengthCheckerLinterTest extends FlatSpec with Matchers {

  behavior of "LengthCheckerLinter"

  it should "execute without error for a small file" in {
    val checker = new LengthCheckerLinter(new File("testFiles/shortLineFile.hs"), Language.Haskell)
    assert(checker.parseFiles.isEmpty)
  }

  it should "give an error if an input file has lines over 80 characters long" in {
    val checker = new LengthCheckerLinter(new File("testFiles/longLineFile.hs"), Language.Haskell)
    assert(checker.parseFiles.nonEmpty)
  }

  it should "should throw an exception if file does not exist" in {
    assertThrows[NoSuchFileException] {
      val checker = new LengthCheckerLinter(new File("bloop"), Language.Haskell)
      checker.parseFiles
    }
  }
}