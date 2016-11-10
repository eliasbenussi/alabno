package alabno.simple_haskell_marker;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class HaskellSplitterTest {

    final boolean PRINT_RESULTS = false;
    
    String filePath = "samples/macro.hs";

    @Test
    public void isEmptyTest() {
        String test1 = "hello\nworld";
        String test2 = "\n\n  \n";
        String test3 = "\n";
        
        HaskellSplitter splitter = new HaskellSplitter(filePath);
        
        assertTrue(!splitter.isEmpty(test1));
        
        assertTrue(splitter.isEmpty(test2));
        
        assertTrue(splitter.isEmpty(test3));
    }
    
    /**
     * Tests the splitter on the whole input Haskell file
     */
    @Test
    public void splitTest1() {
        HaskellSplitter splitter = new HaskellSplitter(filePath);
        List<HaskellBlock> output = splitter.split();
        if (PRINT_RESULTS) {
            for (int i = 0; i < output.size(); i++) {
//                System.out.println(i + "\t" + output.get(i).getBlockText());
                System.out.println(i + "\t" + output.get(i));
            }
        }
        assertEquals(output.size(), 15);
    }

}
