package postprocessor;


import json_parser.Error;
import json_parser.MicroServiceOutput;
import java.util.*;

public class Aggregator {

    private List<MicroServiceOutput> microServiceOutputs;

    public Aggregator(List<MicroServiceOutput> microServiceOutputs) {
        this.microServiceOutputs = microServiceOutputs;
    }

    /**
     * Regroup annotations from all MicroServices' json output provided
     * by errorType inside new JSON file.
     *
     * @return JSONArray with final, ordered output
     */
    public List<Error> aggregate() {

        List<Error> annotations = new ArrayList<>();

        for (MicroServiceOutput microServiceOutput : microServiceOutputs) {

            if (!microServiceOutput.getErrors().isEmpty()) {
                System.out.println("\nA MicroService failed to produce a valid output.\n" +
                        "Details on the errors:\n");
                microServiceOutput.getErrors().stream().forEach(System.out::println);
            }
            annotations.addAll(microServiceOutput.getAnnotations());

        }
        return annotations;
    }
}
