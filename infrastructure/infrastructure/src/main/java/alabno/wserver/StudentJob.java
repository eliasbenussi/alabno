package alabno.wserver;

import java.io.File;
import java.io.PrintWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.utils.FileUtils;
import alabno.utils.SubprocessUtils;

/**
 * Represents the script of a single student
 *
 */
public class StudentJob {

    private final String jsonLocation;
    private final String exerciseType;
    
    /**
     * @param jsonLocation path where the json
     * produced by the postprocessor is located
     */
    public StudentJob(String jsonLocation, String exerciseType) {
        this.jsonLocation = jsonLocation;
        this.exerciseType = exerciseType;
    }
    
    /**
     * @return the contents of the final json
     * output of the student
     */
    public String readPostProcessorOutput() {
        return FileUtils.read_file(jsonLocation);
    }

    public String getJsonLocation() {
        return jsonLocation;
    }
    
    private JsonParser getPostproJson() {
        String postproContent = readPostProcessorOutput();
        return new JsonParser(postproContent);
    }

    public SourceDocument amend(String fileName, int lineno, String annType, String annotation) {
        if ("ok".equals(annotation)) {
            annotation = "";
        }
        
        String desiredFile = toAbsolute(fileName);
        
        // Read the source file
        SourceDocument doc = new SourceDocument(fileName, desiredFile);

        JsonParser parser = getPostproJson();
        
        // get the annotations part
        JsonArrayParser annotations = parser.getArrayParser("annotations");

        for (JsonParser ann : annotations) {
            
            Annotation anAnnotation = new Annotation(ann);
            
            String aFileName = anAnnotation.getFilename();
            int aLineNo = anAnnotation.getLineno();
            
            if (desiredFile.equals(aFileName) && lineno == aLineNo) {
                anAnnotation.amendError(ann.getObject(), annType, annotation);
                rewriteJson(parser);
                return doc;
            }
        }
        
        // If loop reaches, nothing was found
        System.out.println("Could not find entry to be amended. Adding a new one...");
        Annotation newAnnotation = new Annotation(annType, desiredFile, lineno, 1, annotation);
        JSONObject newAnn = newAnnotation.toJsonObject();
        
        // append to the existing array
        JSONArray annotationsArray = parser.getArray("annotations");
        if (annotationsArray == null) {
            annotationsArray = new JSONArray();
        }
        annotationsArray.add(newAnn);
        parser.getObject().put("annotations", annotationsArray);
        
        rewriteJson(parser);
        return doc;
        
    }
    
    private void rewriteJson(JsonParser parser) {
        try {
            SubprocessUtils.call("rm " + jsonLocation);
            File jsonFile = new File(jsonLocation);
            PrintWriter writer = new PrintWriter(jsonFile);
            writer.print(parser.getObject().toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String toAbsolute(String relpath) {
        // utility to convert to absolute path based on the relative one
        // the 17 removes the final part of the path containing _out/postpro.json
        return jsonLocation.substring(0, Math.max(0, jsonLocation.length() - 17)) + "/" + relpath;
    }
    
    public String getExerciseType() {
        return exerciseType;
    }

    public SourceDocument getSourceDocument(String fileName) {

        String desiredFile = toAbsolute(fileName);
        
        // Read the source file
        return new SourceDocument(fileName, desiredFile);
    }

    public String getMark() {
        JsonParser parser = getPostproJson();
        return parser.getString("letter_score");
    }
}
