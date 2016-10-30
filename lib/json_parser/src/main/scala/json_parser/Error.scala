package json_parser

/**
  * Class representing mistakes
  *
  * @param msg    Message of the error
  * @param file   Name of the file erroring
  * @param lineNo Line index of the mistake
  * @param colNo  the column number of the mistake
  * @param t      the type of this mistake
  */
class Error(msg: String, file: String, lineNo: Int, colNo: Int, t: String) {

  def getMsg = msg

  def getLineNo = lineNo

  def getColNo = colNo

  def getFile = file

  def getType = t

  override def toString: String = s"<${t.toUpperCase}> $file:$lineNo:$colNo:$msg"

  override def equals(obj: scala.Any): Boolean = obj match {
    case obj: Error =>
      obj.getColNo.equals(colNo) &&
      obj.getFile.equals(file) &&
      obj.getLineNo.equals(lineNo) &&
      obj.getMsg.equals(msg) &&
      obj.getType.equals(t)
    case _ => false
  }

  override def hashCode(): Int = {
    val tmp = ((msg.hashCode << 31) + (lineNo.hashCode() << 7)) << 31
    (tmp + colNo << 27 + file.hashCode << 7) + t.hashCode
  }
}

object Error {
  def apply(msg: String, file: String, lineNo: Int, colNo: Int, t: String): Error = new Error(msg, file, lineNo, colNo, t)
}
