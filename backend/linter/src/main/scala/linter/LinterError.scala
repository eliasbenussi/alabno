package linter

/**
  * Class representing mistakes
  * @param msg Message of the error
  * @param file Name of the file erroring
  * @param position Line index of the mistake
  */
class LinterError(msg: String, file: String, position: Int) {
  override def toString: String = s"$file:$position: $msg"
}
