package json_parser

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

  def getMsg = msg

  def getLineNo = lineNo

  def getColNo = colNo

  def getFile = file

  def getType = t

  override def toString: String = s"<${t.toUpperCase}> $file:$lineNo:$colNo:$msg"
}

object Error {
  def apply(msg: String, file: String, lineNo: Int, colNo: Int, t: String): Error = new Error(msg, file, lineNo, colNo, t)
}
