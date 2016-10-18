package postprocessor;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostProcessor {

    public static void main(String[] args) {

        if (args.length <= 1) {
            throw new IllegalArgumentException(
                    "Not enough argument!\nExpected arguments: <language> <input_json_paths>+ <output_json_path>"
            );
        }

        String language = args[0];
        List<String> inputJsonPaths = new ArrayList<String>();
        int i;
        for (i = 1; i < args.length - 1; i++) {
            inputJsonPaths.add(args[i]);
        }
        String outputJsonPath = args[i + 1];

        Aggregator aggregator = new Aggregator(inputJsonPaths);
        JSONObject aggregatedErrors = aggregator.aggregate();


    }
}
