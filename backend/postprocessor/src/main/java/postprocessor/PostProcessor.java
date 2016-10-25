package postprocessor;

import json_parser.AggregatorOutputParser;
import json_parser.Error;
import json_parser.MicroServiceOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostProcessor {

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException(
                    "Expected arguments: " +
                            "<language> " +
                            "<input_json_paths>+" +
                            "<output_json_path>"
            );
        }

        // Collect paths of MicroServices JSON outputs
        String language = args[0];
        List<String> inputPaths = new ArrayList<>();
        int i;
        for (i = 1; i < args.length - 1; i++) {
            inputPaths.add(args[i]);
        }
        String outputJsonPath = args[i];

        // Wrap outputs in MicroServiceOutput objects.
        // In this way we have a versatile list of containers to pass around w/o dealing with JSON directly.
        List<MicroServiceOutput> microServiceOutputs = PostProcessorUtils.getMicroServiceOutputsFromPaths(inputPaths);

        // Aggregate errors from microservices
        Aggregator aggregator = new Aggregator(microServiceOutputs);
        List<Error> aggregatedErrors = aggregator.aggregate();

        // Get score based on aggregated output
        Scorer scorer = new Scorer(microServiceOutputs);
        Double scores = scorer.getScore();
        String letterScore = scorer.getLetterGrade();

        AggregatorOutputParser.writeFile(new File(outputJsonPath), aggregatedErrors, letterScore, scores);
    }
}
