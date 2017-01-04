package javamarker;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Manages the Java source codes and runs the classifier on them. Produces
 * the required JSON output at the end of the process
 *
 */
public class JavaMarker {

    private ScriptClassifier javaClassifier;
    private Arguments arguments;
    private CategoryConverterInterface categoryConverter;
    private JSONObject outputObject = null;

    public JavaMarker(ScriptClassifier javaClassifier, Arguments arguments,
                         CategoryConverterInterface categoryConverter) {
        this.javaClassifier = javaClassifier;
        this.arguments = arguments;
        this.categoryConverter = categoryConverter;
    }

    public void mark() {
        List<JavaSplitDocument> documents = new ArrayList<>();

        for (String JavaScriptPath : arguments.getJavaInputs()) {

            // create the JavaSplitDocument
            JavaSplitter splitter = new JavaSplitter(JavaScriptPath);
            List<JavaBlock> blocks = splitter.split();
            JavaSplitDocument document = new JavaSplitDocument(JavaScriptPath, blocks);

            // run the classifier on the document
            javaClassifier.classify(document);

            documents.add(document);
        }

        outputObject = generateOutput(documents);
    }

    public void writeOutput() {
        if (outputObject == null) {
            return;
        }
        // Write it to output file
        try {
            PrintWriter writer = new PrintWriter(arguments.getOutputJsonPath(), "UTF-8");
            writer.println(outputObject.toJSONString());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    JSONObject getOutputObject() {
        return outputObject;
    }

    private JSONObject generateOutput(List<JavaSplitDocument> documents) {
        JSONObject obj = new JSONObject();
        obj.put("score", calculateScore(documents));

        // generate annotations
        JSONArray annotations = new JSONArray();

        for (JavaSplitDocument doc : documents) {
            addAnnotations(annotations, doc);
        }
        obj.put("annotations", annotations);
        return obj;
    }

    private void addAnnotations(JSONArray annotations, JavaSplitDocument doc) {
        for (JavaBlock block : doc.getBlocks()) {
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

    private int calculateScore(List<JavaSplitDocument> documents) {
        int totalNumberBlocks = 0;

        // Calculate total number of blocks
        for (JavaSplitDocument doc : documents) {
            totalNumberBlocks += doc.getBlocks().size();
        }

        int totalNumberOk = 0;

        // Calculate number of OKs
        for (JavaSplitDocument doc : documents) {
            for (JavaBlock block : doc.getBlocks()) {
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