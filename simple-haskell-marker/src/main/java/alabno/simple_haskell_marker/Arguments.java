package alabno.simple_haskell_marker;

import java.util.ArrayList;
import java.util.List;

public class Arguments {

    private List<String> haskellInputs = new ArrayList<>();
    private String outputJsonPath;
    
    public Arguments(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            haskellInputs.add(args[i]);
        }
        outputJsonPath = args[args.length - 1];
    }
    
    public List<String> getHaskellInputs() {
        return haskellInputs;
    }
    
    public String getOutputJsonPath() {
        return outputJsonPath;
    }

}
