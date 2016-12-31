package alabno.simple_haskell_marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arguments {

    private List<String> haskellInputs = new ArrayList<>();
    private String outputJsonPath = null;
    private String trainingDataPath = null;

    public Arguments(String[] args) {
        try {
            this.trainingDataPath = args[0];
            this.outputJsonPath = args[1];
        } catch (Exception e) {
            System.out.println("Not enough arguments...");
        }

        haskellInputs.addAll(Arrays.asList(args).subList(2, args.length));
    }

    public List<String> getHaskellInputs() {
        return haskellInputs;
    }

    public String getOutputJsonPath() {
        return outputJsonPath;
    }

    public String getTrainingDataPath() {
        return trainingDataPath + ".train";
    }
    
    public String getCategoryDataPath() {
        return trainingDataPath + ".csv";
    }

}
