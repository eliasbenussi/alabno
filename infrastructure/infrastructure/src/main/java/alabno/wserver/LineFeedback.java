package alabno.wserver;

public class LineFeedback {

    private String lineContent;
    private String annotation = "";

    public LineFeedback(String lineContent, String annotation) {
        this.lineContent = lineContent;
        this.annotation = annotation;
    }

    public LineFeedback(String lineContent) {
        this.lineContent = lineContent;
    }

    public String getLineContent() {
        return lineContent;
    }

    public String getAnnotation() {
        return annotation;
    }
}
