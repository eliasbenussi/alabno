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
        String[] args = {"input1.hs", "dir/input2.hs", "output.json"};
        
        Arguments arguments = new Arguments(args);
        
        assertEquals(arguments.getHaskellInputs().get(0), "input1.hs");
        assertEquals(arguments.getHaskellInputs().get(1), "dir/input2.hs");
        assertEquals(arguments.getHaskellInputs().size(), 2);
        
        assertEquals(arguments.getOutputJsonPath(), "output.json");
    }

}
