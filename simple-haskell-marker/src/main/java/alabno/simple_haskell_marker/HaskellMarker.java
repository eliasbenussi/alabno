package alabno.simple_haskell_marker;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Manages the Haskell source codes and runs the Classifier on them. Produces
 * the required JSON output at the end of the process
 *
 */
public class HaskellMarker {

    private HaskellClassifier haskellClassifier;
    private Arguments arguments;
    private CategoryConverter categoryConverter;

    public HaskellMarker(HaskellClassifier haskellClassifier, Arguments arguments,
            CategoryConverter categoryConverter) {
        this.haskellClassifier = haskellClassifier;
        this.arguments = arguments;
        this.categoryConverter = categoryConverter;
    }

    public void mark() {
        List<HaskellSplitDocument> documents = new ArrayList<>();

        for (String haskellScriptPath : arguments.getHaskellInputs()) {
            // create the HaskellSplitDocument
            HaskellSplitter splitter = new HaskellSplitter(haskellScriptPath);
            List<HaskellBlock> blocks = splitter.split();
            HaskellSplitDocument document = new HaskellSplitDocument(haskellScriptPath, blocks);

            // run the classifier on the document
            haskellClassifier.classify(document);

            documents.add(document);
        }

        JSONObject outputObject = generateOutput(documents);

        // Write it to output file
        try {
            PrintWriter writer = new PrintWriter(arguments.getOutputJsonPath(), "UTF-8");
            writer.println(outputObject.toJSONString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private JSONObject generateOutput(List<HaskellSplitDocument> documents) {
        JSONObject obj = new JSONObject();
        obj.put("score", calculateScore(documents));

        // generate annotations
        JSONArray annotations = new JSONArray();

        for (HaskellSplitDocument doc : documents) {
            addAnnotations(annotations, doc);
        }
        obj.put("annotations", annotations);
        return obj;
    }

    private void addAnnotations(JSONArray annotations, HaskellSplitDocument doc) {
        for (HaskellBlock block : doc.getBlocks()) {
            String ann = block.getAnnotation();
            // NOTE should also ignore comments, which will be classified as ok
            if (ann != null && !ann.equals("ok")) {
                JSONObject annotationObject = new JSONObject();
                annotationObject.put("errortype", categoryConverter.getErrorType(ann));
                annotationObject.put("filename", doc.getName());
                annotationObject.put("lineno", block.getLineNumber());
                annotationObject.put("charno", 1);
                annotationObject.put("text", categoryConverter.getDescription(ann));
                annotations.add(annotationObject);
            }
        }
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
