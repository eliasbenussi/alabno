package javamarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class OverlapBlockSplitter {

    private String filePath;
    private List<JavaBlock> container = new ArrayList<>();

    // Default values for block size and offset
    private int blockSize = 200;
    private int blockOffset = 50;

    public OverlapBlockSplitter(String filePath) {
        this.filePath = filePath;
    }

    public List<JavaBlock> getContainer() {
        return container;
    }

    public void setBlockSize(int newBlockSize) {
        this.blockSize = newBlockSize;
    }

    public void setBlockOffset(int newBlockOffset) {
        this.blockOffset = newBlockOffset;
    }

    /**
     * Split the file @filePath in overlapping blocks of size blockSize.
     * The blocks are collected in the container.
     */
    public void split() {

        Map<Integer, Integer> inverseMap = new HashMap<>();
        int charIndex = 0;
        int lineIndex = 1;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder();

        // Initialize inverse map
        while (scanner.hasNextLine()) {
            String theLine = scanner.nextLine();
            builder.append(theLine);
            char[] line = theLine.toCharArray();
            for (char c : line) {
                inverseMap.put(charIndex, lineIndex);
                charIndex++;
            }
            lineIndex++;
        }

        scanner.close();

        String fileContent = builder.toString();

        int i = 0;
        while (i < fileContent.length()) {
            int j = 0;

            StringBuilder contentBuilder = new StringBuilder();
            int blockLineNumber = inverseMap.get(i);

            while (j < blockSize && i < fileContent.length()) {
                contentBuilder.append(fileContent.charAt(i));
                i++;
                j++;
            }
            String blockContent = contentBuilder.toString();
            JavaBlock block = new JavaBlock(blockLineNumber, blockContent);
            block.pad(blockSize);
            container.add(block);

            if (i >= fileContent.length()) {
                break;
            }
            i -= blockSize - blockOffset;
        }


    }
}
