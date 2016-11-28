package mark_marker.trainer

import java.io._
import java.sql.DriverManager
import java.sql.Connection

import edu.stanford.nlp.classify.{Classifier, ColumnDataClassifier}

import scala.io.Source

class DatabaseConnector {
  private val driver = "com.mysql.cj.jdbc.Driver"
  private val url = "jdbc:mysql://tc.jstudios.ovh:3306/Automarker"
  private val username = "python"
  private val psw = findPsw("dbpass.txt")
  private var connection: Connection = _

  def findPsw(path: String) = {
    Source.fromFile(new File(path)).mkString
  }

  def connect() = {
    Class.forName(driver)
    DriverManager.getConnection(url, username, psw)
    connection = DriverManager.getConnection(url, username, psw)
  }

  def close() = connection.close()

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

  def getSerialisedClassifier(exercise: String): Classifier[String, String] = {
    val stat = connection.prepareStatement(s"SELECT serialized FROM MarkMarkerTraining WHERE exercise_name = ?")
    stat.setString(1, exercise)
    val resultSet = stat.executeQuery
    var ois: ObjectInputStream = null
    var cl: Classifier[String, String] = null
    while (resultSet.next) {
      val b = resultSet.getBlob("serialized")
      ois = new ObjectInputStream(b.getBinaryStream)
      cl = ois.readObject().asInstanceOf[Classifier[String, String]]
    }
    resultSet.close()
    cl
  }

  def addTrainingAndCdc(exercise: String, trainingData: String, serialisedClassifier: Classifier[String, String]) = {
    val fis: ByteArrayInputStream = serialiseCdc(serialisedClassifier)
    if (getTrainingData(exercise) != "") {
      updateSerialisedClassifier(exercise, serialisedClassifier)
    }
    val stat = connection.prepareStatement(s"INSERT INTO MarkMarkerTraining (exercise_name, training_set, serialized) VALUES (?, ?, ?)")
    stat.setString(1, exercise)
    stat.setString(2, trainingData)
    stat.setBlob(3, fis)
    stat.executeUpdate()
    stat.close()
    fis.close()
  }

  def updateTrainingData(exercise: String, trainingData: String) = {
    val stat = connection.prepareStatement("UPDATE MarkMarkerTraining SET training_set = ? WHERE exercise_name = ?")
    stat.setString(2, exercise)
    stat.setString(1, trainingData)
    stat.executeUpdate()
    stat.close()
  }

  def updateSerialisedClassifier(exercise: String, serialisedClassifier: Classifier[String, String]) = {
    val fis: ByteArrayInputStream = serialiseCdc(serialisedClassifier)
    val stat = connection.prepareStatement("UPDATE MarkMarkerTraining SET training_set = ? WHERE exercise_name = ?")
    stat.setString(2, exercise)
    stat.setBlob(1, fis)
    stat.executeUpdate()
    stat.close()
  }

  def serialiseCdc(serialisedClassifier: Classifier[String, String]): ByteArrayInputStream = {
    val fos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(serialisedClassifier)
    oos.close()
    val b = fos.toByteArray
    fos.close()
    new ByteArrayInputStream(b)
  }
}
