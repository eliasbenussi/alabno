package javamarker;

/**
 * This class represents a single block of Java Code
 */
public class JavaBlock {

    private int lineNumber;
    private String content;
    private String annotation = null;

    public JavaBlock(int lineNumber, String content) {
        this.lineNumber = lineNumber;
        this.content = content.replace("\n", "\\n").replace("\t", "    ");
    }

    public void pad(int desiredBlockSize) {
        while (content.length() < desiredBlockSize) {
            content += " ";
        }
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getContent() {
        return content;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "JavaBlock [lineNumber: " + lineNumber + ", content: " + content + "]";
    }
}
