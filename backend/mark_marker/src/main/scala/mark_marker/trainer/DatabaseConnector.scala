package mark_marker.trainer

import java.io._
import java.sql.{Connection, DriverManager}

import scala.io.Source

class DatabaseConnector(dbName: String) {
  private val driver = "com.mysql.cj.jdbc.Driver"
  private val username = "python"
  private val url = "jdbc:mysql://alabno.jstudios.ovh:3306/Automarker"
  private val locurl = "jdbc:mysql://localhost:3306/Automarker"
  private val psw = findPsw("dbpass.txt")
  private var connection: Connection = _

  def findPsw(path: String) = {
    Source.fromFile(new File(path)).mkString.trim
  }

  def connect() = {
    Class.forName(driver)
    connection = DriverManager.getConnection(if (sys.env("ALABNOLOCAL") == "1") locurl else url, username, psw)
  }

  def close() = connection.close()

  def getTrainingData(exercise: String): String = {
    val stat = connection.prepareStatement(s"SELECT training_data FROM $dbName WHERE exercise = ?")
    stat.setString(1, exercise)
    val resultSet = stat.executeQuery
    val sb = new StringBuilder
    while (resultSet.next)
      sb.append(resultSet.getString("training_data"))
    resultSet.close()
    sb.toString()
  }


  def addTrainingData(exercise: String, trainingData: String) = {
    val stat = connection.prepareStatement(s"INSERT INTO $dbName (exercise, training_data) VALUES (?, ?)")
    stat.setString(1, exercise)
    stat.setString(2, trainingData)
    stat.executeUpdate
    stat.close()
  }

  def updateTrainingData(exercise: String, trainingData: String) = {
    val stat = connection.prepareStatement(s"UPDATE $dbName SET training_data = ? WHERE exercise = ?")
    stat.setString(2, exercise)
    stat.setString(1, trainingData)
    stat.executeUpdate()
    stat.close()
  }


}
