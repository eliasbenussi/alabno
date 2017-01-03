package alabno.simple_haskell_marker;

import static org.junit.Assert.*;

import org.junit.Test;

public class HaskellBlockTest {

    @Test
    public void blocktestNoAnnotation() {
        HaskellBlock haskellBlock = new HaskellBlock(12, "sample text");
        
        assertEquals(haskellBlock.getAnnotation(), null);
        
        assertEquals(haskellBlock.getBlockText(), "sample text");
        
        assertEquals(haskellBlock.getLineNumber(), 12);
    }
    
    @Test
    public void blocktestWithAnnotation() {
        HaskellBlock haskellBlock = new HaskellBlock(12, "sample text");
        haskellBlock.setAnnotation("-10000");
        
        assertEquals(haskellBlock.getAnnotation(), "-10000");
        
        assertEquals(haskellBlock.getBlockText(), "sample text");
        
        assertEquals(haskellBlock.getLineNumber(), 12);
    }

}
