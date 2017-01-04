package javamarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JavaSplitter {

    String filePath;
    File file;

    // Default values for block size and offset
    private final int blockSize = 200;
    private final int blockOffset = 50;
    Map<Integer, Integer> inverseMap = new HashMap<>();
    List<String> lines = new ArrayList<>();
    String fileContent;

    public JavaSplitter(String filePath) {
       this.filePath = filePath;
       init();
    }

    private void init() {
        this.file = new File(filePath);

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int charIndex = 0;
        int lineIndex = 1;

        StringBuilder builder = new StringBuilder();

        // Initialize inverse map
        while (scanner.hasNextLine()) {
            String theLine = scanner.nextLine();
            lines.add(theLine);
            builder.append(theLine);
            for (char c : theLine.toCharArray()) {
                inverseMap.put(charIndex, lineIndex);
                charIndex++;
            }
            lineIndex++;
        }
        scanner.close();

        fileContent = builder.toString();
    }

    /**
     * Split the file @filePath in overlapping blocks of size blockSize.
     * The blocks are collected in the container.
     */
    public List<JavaBlock> split() {

        List<JavaBlock> container = new ArrayList<>();

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
        return container;
    }
}
