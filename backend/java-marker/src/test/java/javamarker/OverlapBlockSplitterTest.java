package javamarker;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OverlapBlockSplitterTest {

    @Test
    public void splitsFileInOverlappingBlocksCorrectly() {

        String filePath = "src/test/testFiles/toSplit.txt";
        OverlapBlockSplitter splitter = new OverlapBlockSplitter(filePath);
        splitter.split();
        List<JavaBlock> blocks = splitter.getContainer();
        assertEquals(2, blocks.size());

    }


}
