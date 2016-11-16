package alabno.wserver;

import java.io.File;
import java.io.PrintWriter;

import org.json.simple.JSONObject;

import alabno.utils.FileUtils;
import alabno.utils.SubprocessUtils;

/**
 * Represents the script of a single student
 *
 */
public class StudentJob {

    private String jsonLocation;
    
    /**
     * @param jsonLocation path where the json
     * produced by the postprocessor is located
     */
    public StudentJob(String jsonLocation) {
        this.jsonLocation = jsonLocation;
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

    public void amend(String fileName, int lineno, String annType, String annotation) {
        if ("ok".equals(annotation)) {
            annotation = "";
        }
        
        String desiredFile = toAbsolute(fileName);
        
        // Read the postpro file
        String postproContent = readPostProcessorOutput();
        
        JsonParser parser = new JsonParser(postproContent);
        
        // get the annotations part
        JsonArrayParser annotations = parser.getArrayParser("annotations");

        for (JsonParser ann : annotations) {
            
            String aFileName = ann.getString("filename");
            int aLineNo = ann.getInt("lineno");
            
            if (desiredFile.equals(aFileName) && lineno == aLineNo) {
                JSONObject amended = ann.getObject();
                amended.put("errortype", annType);
                amended.put("text", annotation);
                
                rewriteJson(parser);
                return;
            }
        }
        
        // If loop reaches, nothing was found
        System.out.println("Could not find entry to be amended");
        
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
        return jsonLocation.substring(0, Math.max(0, jsonLocation.length() - 17)) + "/" + relpath;
    }
}
