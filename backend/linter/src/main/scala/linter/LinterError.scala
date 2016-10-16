package linter

/**
  * Class representing mistakes
  *
  * @param msg    Message of the error
  * @param file   Name of the file erroring
  * @param lineNo Line index of the mistake
  * @param colNo  the column number of the mistake
  */
class LinterError(msg: String, file: String, lineNo: Int, colNo: Int) {
  val _msg = msg
  val _lineNo = lineNo
  val _colNo = colNo

  override def toString: String = s"$file:$lineNo:$colNo: $msg"

}
