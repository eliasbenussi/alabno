package mark_marker.trainer

import java.io._

import edu.stanford.nlp.classify.{Classifier, ColumnDataClassifier}


/**
  * Trainer object used to retrieve training data
  */
object Trainer {

  private final val trainPath = ""

  /**
    * Produces a classifier for an exercise, whether it already exists or not
    * @param exercise name of the exercise
    * @param prop properties to be used for the classifier
    * @param databaseConnector the database connector
    * @return a ColumnDataClassifier and a Classifier
    */
  def getCdc(exercise: String, prop: String, databaseConnector: DatabaseConnector): (ColumnDataClassifier, Classifier[String, String]) = {
    val f = new File(s"$trainPath$exercise.bin")
    val cdc = new ColumnDataClassifier(prop)
    if (f.exists()) return (cdc, deserialiseCdc(f))
    val cl = generateCdc(cdc, exercise, prop, databaseConnector)
    updateSerialisedClassifier(exercise, cl)
    (cdc, cl)
  }

  private def updateSerialisedClassifier(exercise: String, serialisedClassifier: Classifier[String, String]): Unit = {
    val b = serialiseCdc(serialisedClassifier)
    val fos = new FileOutputStream(new File(s"$trainPath$exercise.bin"))
    fos.write(b)
    fos.close()
  }

  private def serialiseCdc(serialisedClassifier: Classifier[String, String]): Array[Byte] = {
    val fos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(fos)
    oos.writeObject(serialisedClassifier)
    oos.close()
    fos.close()
    fos.toByteArray
  }

  private def generateCdc(cdc: ColumnDataClassifier, exercise: String, prop: String, db: DatabaseConnector): Classifier[String, String] = {
    val s = db.getTrainingData(exercise)
    val trainFile = new File(s"$trainPath$exercise.train")
    val fw = new FileWriter(trainFile)
    fw.write(s)
    fw.close()
    cdc.makeClassifier(cdc.readTrainingExamples(trainFile.getPath))
  }

  private def deserialiseCdc(file: File): Classifier[String, String] = {
    val fis = new FileInputStream(file)
    val OIS = new ObjectInputStream(fis)
    OIS.readObject().asInstanceOf[Classifier[String, String]]

  }
}
