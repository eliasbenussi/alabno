package linter


/*
 * Companion class used to create instances of OutputGenerator
 */
object OutputGenerator {
  def getScore = 100.0d - score
  private var score = 0.0d
  def addScore(value: Double) = score += value
}
