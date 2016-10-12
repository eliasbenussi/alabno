package linter

/**
  * Object used to represent languages
  */
object Language extends Enumeration {
  /**
    * <p> Enumeration used to represent supported programming languages </p>
    * <p> Used as a representation throughout the whole linter </p>
    */
  val Haskell, Java, Ruby, Go, Other = Value

  /**
    * Checks whether the exension of a file matches the one used by a language
    * @param name The filename to be checked
    * @param language The language to compare against
    * @return
    *         <p> True if the extension matches the language </p>
    *         <p> This method will throw an IllegalArgumentException </p>
    *         <p> when trying to match Language.Other </p>
    */
  def matchExtension(name: String, language: Language.Value) = {
    language match {
      case Haskell => name.endsWith(".hs")
      case Java => name.endsWith(".java")
      case Ruby => name.endsWith(".rb")
      case Go => name.endsWith(".go")
      case _ => throw new IllegalArgumentException("Invalid type")
    }
  }

  /**
    * Matches a string to a Language.Value
    * @param str The string to be matched
    * @return A Language.Value representing the string
    */
  def matchString(str: String) = str match {
    case "haskell" => Haskell
    case "ruby" => Ruby
    case "java" => Java
    case "go" => Go
    case _ => Other
  }
}

