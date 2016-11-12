package alabno.simple_haskell_marker;

import java.util.ArrayList;
import java.util.List;

public class Arguments {

    private List<String> haskellInputs = new ArrayList<>();
    private String outputJsonPath;
    private String trainingDataPath;
    
    public Arguments(String[] args) {
//        if (args == null || args.length < 1) {
//            System.out.println("Error: invalid command line arguments");
//            outputJsonPath = null;
//            return;
//        }
//        for (int i = 0; i < args.length - 1; i++) {
//            haskellInputs.add(args[i]);
//        }
//        outputJsonPath = args[args.length - 1];
        
        
    }
    
    public List<String> getHaskellInputs() {
        return haskellInputs;
    }
    
    public String getOutputJsonPath() {
        return outputJsonPath;
    }

}
