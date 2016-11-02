package alabno.simple_haskell_marker;

/**
 * Represents a single block of text in the Haskell source
 *
 */
public class HaskellBlock {

    private int lineNumber;
    private String blockText;
    private String annotation = null;
    
    public HaskellBlock(int lineNumber, String blockText) {
        this.lineNumber = lineNumber;
        this.blockText = blockText.replace("\n", "\\n").replace("\t", "    ");
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    public String getBlockText() {
        return blockText;
    }
    
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    public String getAnnotation() {
        return annotation;
    }

    @Override
    public String toString() {
        return "HaskellBlock [lineNumber=" + lineNumber + ", blockText=" + blockText + "]";
    }

}
