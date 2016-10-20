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
     * @param pathToFile Path to the file being opened
     * @return A JSONObject of the contents of the file
     */
    public static JSONObject obtainJSONFile(String pathToFile) {
        JSONParser parser = new JSONParser();
        JSONObject output = null;
        try {
            output = (JSONObject) parser.parse(new FileReader(pathToFile));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

}
