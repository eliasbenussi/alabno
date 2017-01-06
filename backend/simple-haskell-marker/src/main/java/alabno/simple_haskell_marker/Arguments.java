package alabno.simple_haskell_marker;

import java.util.ArrayList;
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

        for (int i = 2; i < args.length; i++) {
            haskellInputs.add(args[i]);
        }
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
