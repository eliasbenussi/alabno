package alabno.simple_haskell_marker;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class HaskellSplitterTest {

    final boolean PRINT_RESULTS = false;
    
    String filePath = "samples/sequences_model.hs";

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
        assertEquals(output.size(), 20);
        
        assertEquals(output.get(1).getLineNumber(), 3);
        assertEquals(output.get(15).getLineNumber(), 73);
        assertEquals(output.get(16).getLineNumber(), 77);
        assertEquals(output.get(19).getLineNumber(), 85);
        
        assertTrue(output.get(0).getBlockText().contains("module Sequences"));
        assertTrue(output.get(10).getBlockText().contains("Char -> Char"));
        assertTrue(output.get(19).getBlockText().contains("(n + 1)) / (1 - r)"));
    }

}
