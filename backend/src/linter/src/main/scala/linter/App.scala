package linter
import java.io.File

import play.libs.Json
/**
 * @author ${user.name}
 */
object App {

  def main(args : Array[String]) {
    val json = Json.parse(System.in)
    val path = json.findValue("path").asText()
    val language = json.findValue("language").asText()
    val linter = new Linter(new File(path), Language.matchString(language))
    //val out = Json.toJson("mistakes" -> )
    print(linter.parseFiles.toList)
  }

}
