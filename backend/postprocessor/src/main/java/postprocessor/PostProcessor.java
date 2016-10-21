package postprocessor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostProcessor {

    public static void main(String[] args) {
        if (args.length <= 3) {
            throw new IllegalArgumentException(
                    "Expected arguments: " +
                            "<language> " +
                            "<input_json_paths>+" +
                            "<output_json_path>"
            );
        }

        String language = args[0];
        List<String> inputJsonPaths = new ArrayList<>();
        int i;
        for (i = 1; i < args.length - 1; i++) {
            inputJsonPaths.add(args[i]);
        }
        String outputJsonPath = args[i];

        // Aggregate errors from microservices
        Aggregator aggregator = new Aggregator(inputJsonPaths);
        JSONArray jsonAggregatedErrors = aggregator.aggregate();

        // Get score based on aggregated output
        Map<String, Double> microServiceScores = aggregator.getMicroServiceScores(inputJsonPaths);
        Scorer scorer = new Scorer(microServiceScores);
        JSONArray scores = scorer.getScore();

        // Combine the outputs into on JSONObject built from a map
        Map<String, JSONArray> rawOutputs = new HashMap<>();
        rawOutputs.put("scores", scores);
        rawOutputs.put("annotations", jsonAggregatedErrors);
        JSONObject finalOutput = new JSONObject(rawOutputs);
        String jsonFinalOutput = finalOutput.toJSONString();

        // Write to output file specified
        try {
            PrintWriter writer = new PrintWriter(outputJsonPath, "UTF-8");
            writer.println(jsonFinalOutput);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("There was an error writing to: " + outputJsonPath);
        }
    }
}
