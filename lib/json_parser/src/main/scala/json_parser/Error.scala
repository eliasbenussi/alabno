package json_parser

import play.api.libs.json._

/**
  * Created by helicopter88 on 23/10/16.
  */
/**
  * Class representing mistakes
  *
  * @param msg    Message of the error
  * @param file   Name of the file erroring
  * @param lineNo Line index of the mistake
  * @param colNo  the column number of the mistake
  */
class Error(msg: String, file: String, lineNo: Int, colNo: Int, t: String) {
  implicit val _msg = msg
  implicit val _lineNo = lineNo
  implicit val _colNo = colNo
  implicit val _file = file
  implicit val _type = t

  override def toString: String = s"$file:$lineNo:$colNo: $msg"


}

object Error {
  def apply(msg: String, file: String, lineNo: Int, colNo: Int, t: String): Error = new Error(msg, file, lineNo, colNo, t)
}
