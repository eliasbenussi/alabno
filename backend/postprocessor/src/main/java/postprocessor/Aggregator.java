package postprocessor;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Aggregator {

    List<String> jsonPaths;

    public Aggregator(List<String> jsonPaths) {
        this.jsonPaths = jsonPaths;
    }

    /**
     * Regroup annotations from all microservices' json output provided
     * by errorType inside new JSON file.
     * @return JSON object with final, ordered output
     */
    public String aggregate() {

        Map<ErrorType, List<JSONObject>> map = new HashMap<>();

        // Create massive list containing all errors
        JSONParser parser = new JSONParser();
        for (String path : jsonPaths) {
            try {

                // Parse file as JSON object
                Object obj = parser.parse(new FileReader(path));
                JSONObject jsonMicroServiceOutput = (JSONObject) obj;

                String error;
                if ((error = (String) jsonMicroServiceOutput.get("error")) != null) {
                    System.out.println("One microservice failed to produce a valid output. " +
                            "Details on the error: " + error);
                    // TODO: decide what to do in this case. For the moment, skipping output.
                } else {

                    // Get array of annotations from microservice's JSON output
                    JSONArray annotations = (JSONArray) jsonMicroServiceOutput.get("annotations");
                    addAnnotationToMapByErrorType(map, annotations);
                }

            } catch (ParseException e) {
                System.out.println("[EXCEPTION] Could not parse file " + path + ". Aborting.");
                System.exit(-1);
            } catch (IOException e) {
                System.out.println("[EXCEPTION] Error reading file " + path + ". Aborting.");
                System.exit(-1);
            }
        }

        String finalOutput = generateJSONOutput(map);
        return finalOutput;
    }

    /**
     * Generate JSON formatted string containing
     * all annotations arranged by error type in
     * different JSON arrays
     * @param map
     * @return
     */
    private String generateJSONOutput(Map<ErrorType, List<JSONObject>> map) {
        JSONObject finalOutput = new JSONObject();
        for (ErrorType type : ErrorType.values()) {

            List<JSONObject> associatedAnnotations = map.get(type);
            JSONArray jsonArray = new JSONArray();
            for (JSONObject jsonAnnotation : associatedAnnotations) {
                jsonArray.add(jsonAnnotation);
            }
            finalOutput.put(type.toString(), associatedAnnotations);
        }
        return finalOutput.toJSONString();
    }

    /**
     * Add annotations in map arranging by error type
     * @param map
     * @param annotations
     */
    private void addAnnotationToMapByErrorType(Map<ErrorType, List<JSONObject>> map, JSONArray annotations) {
        Iterator<JSONObject> annotationsIterator = annotations.iterator();
        while (annotationsIterator.hasNext()) {
            JSONObject annotation = annotationsIterator.next();

            String jsonErrorType = (String) annotation.get("errortype");
            ErrorType errorType = convertStringToErrortype(jsonErrorType);

            List<JSONObject> jsonListByErrorType = new ArrayList<>();
            if (map.containsKey(errorType)) {
                jsonListByErrorType = map.get(errorType);
            }
            jsonListByErrorType.add(annotation);
            map.put(errorType, jsonListByErrorType);
        }
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
