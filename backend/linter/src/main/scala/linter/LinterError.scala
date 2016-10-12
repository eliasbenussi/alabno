package linter

/**
  * Created by helicopter88 on 11/10/16.
  */
class LinterError(msg: String, file: String, position: Int) {
  override def toString: String = s"$file:$position: $msg"
}
