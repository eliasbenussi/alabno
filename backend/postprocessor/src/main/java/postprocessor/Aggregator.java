package postprocessor;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Aggregator {

    List<String> jsonPaths;

    public Aggregator(List<String> jsonPaths) {
        this.jsonPaths = jsonPaths;
    }

    public JSONObject aggregate() {

        Map<ErrorType, List<JSONObject>> map;

        JSONObject aggregatedList = new JSONObject();

        // Create massive list containing all errors
        JSONParser parser = new JSONParser();;
        for (String path : jsonPaths) {
            try {
                Object obj = parser.parse(new FileReader(path));
                JSONObject jsonMicroServiceOutput = (JSONObject) obj;

                String error;
                if ((error = (String) jsonMicroServiceOutput.get("error")) != null) {
                    System.out.println("One microservice failed to produce a valid output. " +
                            "Details on the error: " + error);
                    // TODO: decide what to do in this case. For the moment, skipping output.
                } else {
                    JSONArray annotations = (JSONArray) jsonMicroServiceOutput.get("annotations");
                    Iterator<JSONObject> annotationsIterator = annotations.iterator();
                    while (annotationsIterator.hasNext()) {
                        JSONObject annotation = annotationsIterator.next();
                        String jsonErrorType = (String) annotation.get("errortype");
                        ErrorType errorType = convertStringToErrortype(jsonErrorType);
                    }
                }
            } catch (ParseException e) {
                System.out.println("[EXCEPTION] Could not parse file " + path + ". Aborting.");
                System.exit(-1);
            } catch (IOException e) {
                System.out.println("[EXCEPTION] Error reading file " + path + ". Aborting.");
                System.exit(-1);
            }
        }



        // Group errors by errorType

        // Generate json object

        return null;
    }

    private ErrorType convertStringToErrortype(String jsonErrorType) {
        ErrorType errorType = null;
        switch(jsonErrorType) {
            case "syntax":
                errorType = ErrorType.SYNTAX;
                break;
            case "semantic":
                errorType = ErrorType.SEMANTIC;
                break;
            case "style":
                errorType = ErrorType.STYLE;
                break;
        }
        return errorType;
    }

}
