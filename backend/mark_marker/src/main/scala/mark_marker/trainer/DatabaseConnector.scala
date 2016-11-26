package mark_marker.trainer

import java.io.{File, FileReader}
import java.nio.CharBuffer
import java.sql.DriverManager
import java.sql.Connection

import scala.io.Source

class DatabaseConnector {
  private val driver = "com.mysql.cj.jdbc.Driver"
  private val url = "jdbc:mysql://tc.jstudios.ovh:3306/Automarker"
  private val username = "python"
  private val psw = findPsw("../dbpass.txt")
  private var connection: Connection = _

  def findPsw(path: String) = {
    Source.fromFile(new File(path)).mkString
  }

  def connect() = {
    Class.forName(driver)
    DriverManager.getConnection(url, username, psw)
    connection = DriverManager.getConnection(url, username, psw)
  }

  def getTrainingData(exercise: String): String = {
    val stat = connection.prepareStatement(s"SELECT training_set FROM MarkMarkerTraining WHERE exercise_name = ?")
    stat.setString(1, exercise)
    val resultSet = stat.executeQuery
    val sb = new StringBuilder
    while (resultSet.next)
      sb.append(resultSet.getString("training_set"))
    resultSet.close()
    sb.toString()
  }

  def addTrainingData(exercise: String, trainingData: String): Unit = {
    if (getTrainingData(exercise) != "") {
      updateDb(exercise, trainingData)
      return
    }
    val stat = connection.prepareStatement(s"INSERT INTO MarkMarkerTraining (exercise_name, training_set) VALUES (?, ?)")
    stat.setString(1, exercise)
    stat.setString(2, trainingData)
    stat.executeUpdate()
    stat.close()
  }

  def updateDb(exercise: String, trainingData: String) = {
    val stat = connection.prepareStatement("UPDATE MarkMarkerTraining SET training_set = ? WHERE exercise_name = ?")
    stat.setString(2, exercise)
    stat.setString(1, trainingData)
    stat.executeUpdate()
    stat.close()
  }

  def close() = connection.close()
}
