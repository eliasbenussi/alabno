package linter

/**
  * Created by helicopter88 on 11/10/16.
  */
object Language extends Enumeration {
  val Haskell, Java, Ruby, Go, Other = Value

  def matchExtension(name: String, language: Language.Value) = {
    language match {
      case Haskell => name.endsWith(".hs")
      case Java => name.endsWith(".java")
      case Ruby => name.endsWith(".rb")
      case Go => name.endsWith(".go")
      case _ => throw new IllegalArgumentException("Invalid type")
    }
  }
  def matchString(str: String) = str match {
    case "haskell" => Haskell
    case "ruby" => Ruby
    case "java" => Java
    case "go" => Go
    case _ => Other
  }
}

