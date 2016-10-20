package postprocessor;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
        JSONObject jsonMicroServiceOutput = null;
        for (String path : jsonPaths) {

            jsonMicroServiceOutput = PostProcessorUtils.obtainJSONFile(path);

            JSONArray errors;
            if (!(errors = (JSONArray) jsonMicroServiceOutput.get("errors")).isEmpty()) {
                System.out.println("A microservice failed to produce a valid output. " +
                        "Details on the errors: " + errors.toString());
                // TODO: decide what to do in this case. For the moment, skipping output.
            } else {

                // Get array of annotations from microservice's JSON output
                JSONArray annotations = (JSONArray) jsonMicroServiceOutput.get("annotations");
                addAnnotationToMapByErrorType(map, annotations);
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
            if (associatedAnnotations != null) {
                for (JSONObject jsonAnnotation : associatedAnnotations) {
                    jsonArray.add(jsonAnnotation);
                }
                finalOutput.put(type.toString(), associatedAnnotations);
            }
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
            ErrorType errorType = PostProcessorUtils.convertStringToErrortype(jsonErrorType);

            List<JSONObject> jsonListByErrorType = new ArrayList<>();
            if (map.containsKey(errorType)) {
                jsonListByErrorType = map.get(errorType);
            }
            jsonListByErrorType.add(annotation);
            map.put(errorType, jsonListByErrorType);
        }
    }



}
