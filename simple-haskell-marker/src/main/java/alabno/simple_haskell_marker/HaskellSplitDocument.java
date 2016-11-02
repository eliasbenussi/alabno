package alabno.simple_haskell_marker;

import java.util.List;

/**
 * Represents an entire Haskell source file split into
 * blocks
 *
 */
public class HaskellSplitDocument {

    private String name;
    private List<HaskellBlock> blocks;
    
    public HaskellSplitDocument(String name, List<HaskellBlock> blocks) {
        this.name = name;
        this.blocks = blocks;
    }
    
    public String getName() {
        return name;
    }
    
    public List<HaskellBlock> getBlocks() {
        return blocks;
    }
    
}
