package alabno.simple_haskell_marker;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArgumentsTest {

    @Test
    public void emptyArguments() {
        String[] args = {};
        
        Arguments arguments = new Arguments(args);
        
        assertEquals(arguments.getHaskellInputs().size(), 0);
        
        assertEquals(arguments.getOutputJsonPath(), null);
    }
    
    @Test
    public void standardCase() {
        String[] args = {"/tmp/someTrain", "output.json", "input1.hs", "dir/input2.hs", };
        
        Arguments arguments = new Arguments(args);
        
        assertEquals(arguments.getHaskellInputs().get(0), "input1.hs");
        assertEquals(arguments.getHaskellInputs().get(1), "dir/input2.hs");
        assertEquals(arguments.getHaskellInputs().size(), 2);
        
        assertEquals(arguments.getOutputJsonPath(), "output.json");
        assertEquals(arguments.getTrainingDataPath(), "/tmp/someTrain.train");
    }

}
