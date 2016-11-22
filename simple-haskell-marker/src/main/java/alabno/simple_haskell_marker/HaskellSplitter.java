package alabno.simple_haskell_marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Splits an input Haskell file into smaller subsections
 * Splitting is based on the amount of empty lines,
 * and beginning of function definitions
 *
 */
public class HaskellSplitter {

    static final int MAX_BLOCK_LENGTH = 10;
    
    String inputPath;

    File inputFile;
    List<String> lines = new ArrayList<>();

    /**
     * @param inputPath the input path of the file to be read
     * 
     * Creates a new HaskellSplitter. 
     */
    public HaskellSplitter(String inputPath) {
        this.inputPath = inputPath;
        init();
    }
    
    public HaskellSplitter(List<String> lines) {
        this.lines = lines;
    }

    /**
     *  Reads the input file and creates the Scanner for reading
     */
    private void init() {
        this.inputFile = new File(inputPath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
    }
    
    /**
     * This class contains variables used to
     * determine whether to split or not
     *
     */
    class SplitConditions {
        boolean lastLineWasEmpty = false;
        int currentBlockLength = 0;
    }
    
    /**
     * @return the list of strings, split according Haskell heuristics
     */
    public List<HaskellBlock> split() {
        // Create the variables used to store the current state,
        // used to determine whether to split or not
        SplitConditions conditions = new SplitConditions();
        
        List<HaskellBlock> haskellBlocks = new ArrayList<>();
        
        StringBuilder currentBlock = new StringBuilder();
        
        String currentLine = null;
        
        int currentLineNumber = 0;
        int blockStartLineNumber = 1;
        
        for (String aLine : lines) {
            currentLine = aLine + "\n";
            currentLineNumber++;
            
            if (splitCondition(currentLine, conditions)) {
                HaskellBlock newBlock = new HaskellBlock(blockStartLineNumber, currentBlock.toString());
                haskellBlocks.add(newBlock);
                
                // append the analyzed line to the new block
                conditions.currentBlockLength = 0;
                currentBlock = new StringBuilder();
                currentBlock.append(currentLine);
                conditions.currentBlockLength++;
                blockStartLineNumber = currentLineNumber;
            } else {
                
                // append the analyzed line to the current block
                currentBlock.append(currentLine);
                conditions.currentBlockLength++;
            }
        }
        
        // add pending last block
        if (currentBlock.length() > 0) {
            HaskellBlock newBlock = new HaskellBlock(blockStartLineNumber, currentBlock.toString());
            haskellBlocks.add(newBlock);
        }
        
        // Reformats special characters, empty lines and tabs
        // into a single line in the output
        return haskellBlocks;
    }

    private boolean splitCondition(String currentLine, SplitConditions conditions) {
        
        if (!isEmpty(currentLine) && conditions.currentBlockLength > MAX_BLOCK_LENGTH) {
            conditions.lastLineWasEmpty = false;
            return true;
        }
        
        if (conditions.lastLineWasEmpty && !isEmpty(currentLine)) {
            conditions.lastLineWasEmpty = false;
            return true;
        }
        
        if (currentLine.contains("::")) {
            conditions.lastLineWasEmpty = false;
            return true;
        }
        
        conditions.lastLineWasEmpty = isEmpty(currentLine);
        return false;
    }

    boolean isEmpty(String line) {
        if (line.isEmpty()) {
            return true;
        }
        
        for (char c : line.toCharArray()) {
            if (!"\n\t\r ".contains("" + c)) {
                return false;
            }
        }
        return true;
    }
    
    private HaskellBlock tryGet(List<HaskellBlock> blocks, int index) {
        try {
            return blocks.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public String getBlockAt(int lineNumber) {
        List<HaskellBlock> blocks = split();
        
        HaskellBlock current = tryGet(blocks, 0);
        HaskellBlock next = tryGet(blocks, 1);
        
        int nextIndex = 1;
        
        while (next != null && next.getLineNumber() <= lineNumber) {
            current = next;
            nextIndex += 1;
            next = tryGet(blocks, nextIndex);
        }
        
        return current.getBlockText();
    }

}
