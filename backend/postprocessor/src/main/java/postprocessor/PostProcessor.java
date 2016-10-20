package postprocessor;

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

        // TODO: Give a final output
        String language = args[0];
        List<String> inputJsonPaths = new ArrayList<>();
        int i;
        for (i = 1; i < args.length - 1; i++) {
            inputJsonPaths.add(args[i]);
        }
        String outputJsonPath = args[i + 1];

        Aggregator aggregator = new Aggregator(inputJsonPaths);
        String jsonAggregatedErrors = aggregator.aggregate();
    }
}
