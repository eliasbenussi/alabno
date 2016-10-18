package postprocessor;

public enum ErrorType {
    SYNTAX("syntax"),
    SEMANTIC("semantic"),
    STYLE("style");

    private String type;
    ErrorType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
