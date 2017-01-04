package javamarker;

import java.util.*;

/**
 * Represents java document splitted in overlapping blocks.
 */
public class JavaSplitDocument {

    private String name;
    private List<JavaBlock> blocks;

    public JavaSplitDocument(String name, List<JavaBlock> blocks) {
        this.name = name;
        this.blocks = blocks;
    }

    public List<JavaBlock> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }
}
