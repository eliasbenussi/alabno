package alabno.simple_haskell_marker;

import json_parser.Error;
import json_parser.MicroServiceOutput;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Haskell source codes and runs the Classifier on them. Produces
 * the required JSON output at the end of the process
 */
public class HaskellMarker {

    private ScriptClassifier haskellClassifier;
    private Arguments arguments;
    private CategoryConverterInterface categoryConverter;
    private MicroServiceOutput outputObject = null;

    public HaskellMarker(ScriptClassifier haskellClassifier, Arguments arguments,
                         CategoryConverterInterface categoryConverter) {
        this.haskellClassifier = haskellClassifier;
        this.arguments = arguments;
        this.categoryConverter = categoryConverter;
    }

    public void mark() {
        List<HaskellSplitDocument> documents = new ArrayList<>();

        for (String haskellScriptPath : arguments.getHaskellInputs()) {
            if (haskellScriptPath.contains("Bench"))
                continue;
            if (haskellScriptPath.contains("Test"))
                continue;
            // create the HaskellSplitDocument
            HaskellSplitter splitter = new HaskellSplitter(haskellScriptPath);
            List<HaskellBlock> blocks = splitter.split();
            HaskellSplitDocument document = new HaskellSplitDocument(haskellScriptPath, blocks);

            // run the classifier on the document
            haskellClassifier.classify(document);

            documents.add(document);
        }

        outputObject = generateOutput(documents);
    }

    public void writeOutput() {
        if (outputObject == null) {
            return;
        }
        // Write it to output file
        outputObject.writeFile(new File(arguments.getOutputJsonPath()));
    }

    MicroServiceOutput getOutputObject() {
        return outputObject;
    }

    private MicroServiceOutput generateOutput(List<HaskellSplitDocument> documents) {

        // generate annotations
        List<Error> annotations = new ArrayList<>();

        for (HaskellSplitDocument doc : documents) {
            annotations.addAll(addAnnotations(doc));
        }

        return new MicroServiceOutput(
                calculateScore(documents),
                annotations,
                new ArrayList<>(),
                new ArrayList<>());
    }

    private List<Error> addAnnotations(HaskellSplitDocument doc) {
        List<Error> annotations = new ArrayList<>();
        for (HaskellBlock block : doc.getBlocks()) {
            String ann = block.getAnnotation();
            // NOTE should also ignore comments, which will be classified as ok
            if (ann != null && !ann.equals("ok") && !ann.equals("comment")) {

                Error annotationObject = new Error(
                        categoryConverter.getDescription(ann),
                        doc.getName(),
                        block.getLineNumber(),
                        1,
                        categoryConverter.getErrorType(ann));

                annotations.add(annotationObject);
            }
        }
        return annotations;
    }

    private int calculateScore(List<HaskellSplitDocument> documents) {
        int totalNumberBlocks = 0;

        // Calculate total number of blocks
        for (HaskellSplitDocument doc : documents) {
            totalNumberBlocks += doc.getBlocks().size();
        }

        int totalNumberOk = 0;

        // Calculate number of OKs
        for (HaskellSplitDocument doc : documents) {
            for (HaskellBlock block : doc.getBlocks()) {
                String annotation = block.getAnnotation();
                if (annotation != null && annotation.equals("ok") && annotation.equals("comment")) {
                    totalNumberOk++;
                }
            }
        }

        if (totalNumberBlocks == 0) {
            return 0;
        } else {
            double ok = totalNumberOk;
            double tot = totalNumberBlocks;
            return (int) ((ok / tot) * 100);
        }
    }

}
