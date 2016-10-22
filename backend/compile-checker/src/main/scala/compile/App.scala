package compile

import java.io.{File, FileInputStream}

import play.api.libs.json.Json
import sys.process._

/**
  * This program uses JSON as input and outputs 0 if the given
  * <p> program compiles, or an integer otherwise.
  * <p> Expected input format: <p>
  * <p> { </p>
  * <p>   "input_directory": "<path to a file>", </p>
  * <p>   "type": "<language to be used>", </p>
  * <p> } </p>
  * <p> Language and linters are optional </p>
 */
object App {

  private var path: File = _

  def main(args : Array[String]) {
    if (args.length != 1) {
      throw new IllegalArgumentException(s"Compile checker <input json> ")
    }
    val inputReader = new FileInputStream(args.apply(0))
    val json = Json.parse(inputReader)
    path = new File((json \ "input_directory").as[String])
    val mLanguage = (json \ "type").asOpt[String]
    val language = mLanguage.getOrElse("haskell")
    print(compileCheck(language, path))
  }

  private def checkHaskell(path: File) = {
    var returnCode = 0
    val haskellFiles = path.list().filter(_ endsWith ".hs")
    // GHC output is generated; can be supressed
    for (file <- haskellFiles) returnCode +=
        s"ghc -i$path/IC -i$path $path/$file".!
    returnCode
  }

  private def checkJava(path: File) = {
    val javaFiles = 
      path.listFiles().filter(e => e.getName.endsWith(".java")).mkString(" ")
    s"javac $javaFiles" !
  }

  private def compileCheck(str: String, path: File): Int = str match {
    case "haskell" => checkHaskell(path)
    case "java" => checkJava(path)
    case _ => 0
  }
}
