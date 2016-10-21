package postprocessor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        String outputJsonPath = args[i + 1];

        Aggregator aggregator = new Aggregator(inputJsonPaths);
        String jsonAggregatedErrors = aggregator.aggregate();

        // Get score based on aggregated output
        Scorer scorer = new Scorer(jsonAggregatedErrors);
        String jsonFinalOutput = scorer.getScore();

        // pass FileWriter into OutputStream and write.
        // Write to output file specified
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputJsonPath, "UTF-8");
            writer.println(jsonFinalOutput);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("There was an error writing to the specified file.");
        }
    }
}
