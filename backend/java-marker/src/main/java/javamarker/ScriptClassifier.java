package javamarker;

/*
 * This is in common with the SimpleHaskellMarker.
 * It would be appropriate to extract a SplitDocument interface
 * and generalize this file you are reading for both markers.
 */
public interface ScriptClassifier {

    void classify(JavaSplitDocument document);
}
