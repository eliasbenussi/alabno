package alabno.simple_haskell_marker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.util.ErasureUtils;

/**
 * This is taken from the demo of the Stanford Classifier
 * It is kept here as bookmark of the major features of the library
 *
 */
class ClassifierDemo {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("Please provide the properties and training files");
			System.exit(1);
		}

		ColumnDataClassifier cdc = new ColumnDataClassifier(args[0]);
		Classifier<String, String> cl = cdc.makeClassifier(cdc.readTrainingExamples(args[1]));
		
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line = "0\t" + line;
			Datum<String, String> d = cdc.makeDatumFromLine(line);
			System.out.println("==>  " + cl.classOf(d));
		}

	}

	public static void demonstrateSerialization() throws IOException, ClassNotFoundException {
		System.out.println("Demonstrating working with a serialized classifier");
		ColumnDataClassifier cdc = new ColumnDataClassifier("examples/cheese2007.prop");
		Classifier<String, String> cl = cdc.makeClassifier(cdc.readTrainingExamples("examples/cheeseDisease.train"));

		// Exhibit serialization and deserialization working. Serialized to
		// bytes in memory for simplicity
		System.out.println();
		System.out.println();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(cl);
		oos.close();
		byte[] object = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(object);
		ObjectInputStream ois = new ObjectInputStream(bais);
		LinearClassifier<String, String> lc = ErasureUtils.uncheckedCast(ois.readObject());
		ois.close();
		ColumnDataClassifier cdc2 = new ColumnDataClassifier("examples/cheese2007.prop");

		// We compare the output of the deserialized classifier lc versus the
		// original one cl
		// For both we use a ColumnDataClassifier to convert text lines to
		// examples
		for (String line : ObjectBank.getLineIterator("examples/cheeseDisease.test", "utf-8")) {
			Datum<String, String> d = cdc.makeDatumFromLine(line);
			Datum<String, String> d2 = cdc2.makeDatumFromLine(line);
			System.out.println(line + "  =origi=>  " + cl.classOf(d));
			System.out.println(line + "  =deser=>  " + lc.classOf(d2));
		}
	}

}