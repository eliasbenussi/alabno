package javamarker;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavaSplitterTest {

    private final int BLOCK_OFFSET = 50;

    @Test
    public void splitsFileInOverlappingBlocksCorrectly() {

        String filePath = "src/test/testFiles/toSplit.txt";
        JavaSplitter splitter = new JavaSplitter(filePath);
        List<JavaBlock> blocks = splitter.split();
        assertEquals(2, blocks.size());
        String content1 = blocks.get(0).getContent();
        String content2 = blocks.get(1).getContent();

        // Test overlapping
        assertEquals(content1.substring(50, 199), content2.substring(0, 149));

    }


}
