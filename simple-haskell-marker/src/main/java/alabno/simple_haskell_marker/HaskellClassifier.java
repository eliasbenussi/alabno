package alabno.simple_haskell_marker;

import java.net.URL;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.ling.Datum;

/**
 * Runs the main text classifier
 *
 */
public class HaskellClassifier implements ScriptClassifier {

    private URL trainingSetPath;
    private URL propertiesPath;
    
    private ColumnDataClassifier cdc;
    private Classifier<String, String> cl;
    
    public HaskellClassifier() {
        this.trainingSetPath = this.getClass().getClassLoader().getResource("hs_basic_training.train");
        this.propertiesPath = this.getClass().getClassLoader().getResource("hs_basic_training.prop");
        
        // Initialize the classifier
        System.out.println("Initializing classifier...");
        this.cdc = new ColumnDataClassifier(propertiesPath.getPath());
        this.cl = cdc.makeClassifier(cdc.readTrainingExamples(trainingSetPath.getPath()));
    }

    @Override
    public void classify(HaskellSplitDocument document) {
        for (HaskellBlock block : document.getBlocks()) {
            try {
                String lineToBeAnalyzed = block.getBlockText();
                String classifierInput = "0\t" + lineToBeAnalyzed;
                Datum<String, String> d = cdc.makeDatumFromLine(classifierInput);
                String classifierGuess = cl.classOf(d);
                block.setAnnotation(classifierGuess);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
