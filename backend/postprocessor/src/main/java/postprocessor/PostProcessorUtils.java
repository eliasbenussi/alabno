package postprocessor;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

/**
 * This class holds utility methods used in the module
 */
public class PostProcessorUtils {

    /**
     * Get JSON file from path
     * @param pathToFile
     * @return
     */
    public static JSONObject obtainJSONFile(String pathToFile) {
        JSONParser parser = new JSONParser();
        JSONObject output = null;
        try {
            Object obj = parser.parse(new FileReader(pathToFile));
            output = (JSONObject) obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Takes string indicating error type (e.g. as retrieved from JSON files)
     * and returns corresponding ErrorType.
     * @param jsonErrorType
     * @return
     */
    public static ErrorType convertStringToErrortype(String jsonErrorType) {
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
