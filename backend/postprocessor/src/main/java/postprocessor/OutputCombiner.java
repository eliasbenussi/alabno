package postprocessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OutputCombiner {

    private List<String> rawOutputs = new ArrayList<>();


    public OutputCombiner(String scores, String jsonAggregatedErrors) {
        this.rawOutputs.add(scores);
        this.rawOutputs.add(jsonAggregatedErrors);
    }

    public String getFinalOutput() {
        StringBuilder finalOutput = new StringBuilder();
        int i = 0;
        finalOutput.append("{");
        for (String s : rawOutputs) {
            if (i == rawOutputs.size() - 1) {
                finalOutput.append(s);
            } else {
                finalOutput.append(s + ",");
            }
            i += 1;
        }
        finalOutput.append("}");
        return finalOutput.toString();
    }
}
