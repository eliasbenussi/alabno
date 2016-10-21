package postprocessor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostProcessor {

    public static void main(String[] args) {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
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
            System.out.println(args[i]);
        }
        String outputJsonPath = args[i];

        Aggregator aggregator = new Aggregator(inputJsonPaths);
        String jsonAggregatedErrors = aggregator.aggregate();

        // Get score based on aggregated output
        Map<String, Double> microServiceScores = aggregator.getMicroServiceScores(inputJsonPaths);
        Scorer scorer = new Scorer(microServiceScores);
        String scores = scorer.getScore();

        // Append the score to the Aggregated output
        OutputCombiner outputCombiner = new OutputCombiner(scores, jsonAggregatedErrors);
        String jsonFinalOutput = outputCombiner.getFinalOutput();

        // Write to output file specified
        try {
            PrintWriter writer = new PrintWriter(outputJsonPath, "UTF-8");
            writer.println(jsonFinalOutput);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("There was an error writing to the specified file.");
        }
    }
}
