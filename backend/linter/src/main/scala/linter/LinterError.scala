package linter

/**
  * Class representing mistakes
  *
  * @param msg    Message of the error
  * @param file   Name of the file erroring
  * @param lineNo Line index of the mistake
  * @param colNo  the column number of the mistake
  */
class LinterError(msg: String, file: String, lineNo: Int, colNo: Int, value: Double, t: String) {
  implicit val _msg = msg
  implicit val _lineNo = lineNo
  implicit val _colNo = colNo
  implicit val _file = file
  implicit val _type = t
  OutputGenerator.addScore(value)
  override def toString: String = s"$file:$lineNo:$colNo: $msg"

}
