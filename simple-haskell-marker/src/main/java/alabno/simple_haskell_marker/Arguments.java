package alabno.simple_haskell_marker;

import java.util.ArrayList;
import java.util.List;

public class Arguments {

    private List<String> haskellInputs = new ArrayList<>();
    private String outputJsonPath = null;
    private String trainingDataPath = null;
    
    public Arguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (i == 0) { // Parse the path to train file
                this.trainingDataPath = args[i];
            } else if (i == 1) { // Parse the output json file
                this.outputJsonPath = args[i];
            } else { // Parse the list of haskell files
                haskellInputs.add(args[i]);
            }
        }
    }
    
    public List<String> getHaskellInputs() {
        return haskellInputs;
    }
    
    public String getOutputJsonPath() {
        return outputJsonPath;
    }
    
    public String getTrainingDataPath() {
        return trainingDataPath;
    }

}
