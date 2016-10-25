package postprocessor;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.*;

public class Aggregator {

    private final List<String> jsonPaths;

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

        // Create a list containing errors from all the microservices
        JSONObject jsonMicroServiceOutput;
        for (String path : jsonPaths) {
            jsonMicroServiceOutput = PostProcessorUtils.obtainJSONFile(path);
            JSONArray errors = (JSONArray) jsonMicroServiceOutput.get("errors");
            if (!errors.isEmpty()) {
                System.out.println("\nA microservice failed to produce a valid output.\n" +
                        "Details on the errors:\n" + errors + "\n\n");
                // TODO: decide what to do in this case. For the moment, skipping output.
            } else {
                // Get array of annotations from microservice's JSON output
                JSONArray annotations = (JSONArray) jsonMicroServiceOutput.get("annotations");
                addAnnotationToMapByErrorType(map, annotations);
            }
        }

        return generateJSONOutput(map);
    }

    /**
     * Generate JSON formatted string containing
     * all annotations arranged by error type in
     * different JSON arrays param map The map containing
     * all the micro-service annotations
     * @return Returns all the errors as a JSON string
     */
    // The JSON library does not cope well with generics, suppress unchecked warnings.
    @SuppressWarnings("unchecked")
    private String generateJSONOutput(Map<ErrorType, List<JSONObject>> map) {
        JSONObject finalOutput = new JSONObject();
        for (ErrorType type : ErrorType.values()) {
            List<JSONObject> associatedAnnotations = map.get(type);
            if (associatedAnnotations != null) {
                finalOutput.put(type.toString(), associatedAnnotations);
            }
        }
        return finalOutput.toJSONString();
    }

    /**
     * Add annotations in map arranging by error type
     * @param map The map containing all the micro-service annotations grouped by error types
     * @param annotations The list with all the micro-service annotations
     */
    // The JSON library does not cope well with generics, suppress unchecked warnings.
    @SuppressWarnings("unchecked")
    private void addAnnotationToMapByErrorType(Map<ErrorType, List<JSONObject>> map, JSONArray annotations) {
        for (JSONObject annotation : (Iterable<JSONObject>) annotations) {
            String jsonErrorType = (String) annotation.get("errortype");
            ErrorType errorType = ErrorType.convertStringToErrorType(jsonErrorType);

            List<JSONObject> jsonListByErrorType = new ArrayList<>();
            if (map.containsKey(errorType)) {
                jsonListByErrorType = map.get(errorType);
            }
            jsonListByErrorType.add(annotation);
            map.put(errorType, jsonListByErrorType);
        }
    }
}
