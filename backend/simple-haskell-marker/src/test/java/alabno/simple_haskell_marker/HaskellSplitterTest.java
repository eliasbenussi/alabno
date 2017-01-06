package alabno.simple_haskell_marker;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class HaskellSplitterTest {

    private final boolean PRINT_RESULTS = false;

    private String filePath = "samples/macro.hs";

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
                System.out.println(i + "\t" + output.get(i));
            }
        }
        assertEquals(output.size(), 15);
    }

    @Test
    public void getBlock() {
        HaskellSplitter splitter = new HaskellSplitter(filePath);

        assertEquals("module MP where\\n\\n", splitter.getBlockTextAt(0));
        assertEquals("module MP where\\n\\n", splitter.getBlockTextAt(1));
        assertEquals("module MP where\\n\\n", splitter.getBlockTextAt(2));
        assertEquals("import System.Environment\\n\\n", splitter.getBlockTextAt(3));
        assertEquals("combine :: String -> [String] -> [String]\\ncombine = error \"TODO: implement combine\"\\n\\n", splitter.getBlockTextAt(31));
        assertTrue(splitter.getBlockTextAt(32).contains("getKeyword"));
        assertTrue(splitter.getBlockTextAt(33).contains("getKeyword"));
        assertTrue(splitter.getBlockTextAt(52).contains("main"));
        assertTrue(splitter.getBlockTextAt(59).contains("main"));
        assertTrue(splitter.getBlockTextAt(60).contains("main"));
    }

}
