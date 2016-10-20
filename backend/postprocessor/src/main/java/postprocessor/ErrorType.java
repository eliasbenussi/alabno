package postprocessor;

public enum ErrorType {
    SYNTAX("syntax", 10.0),
    SEMANTIC("semantic", 1.0),
    STYLE("style", 0.1);

    private String type;
    private double deductedMarks;
    ErrorType(String type, double deductedMarks) {
        this.type = type;
        this.deductedMarks = deductedMarks;
    }

    public double getDeductedMarks() {
        return deductedMarks;
    }

    public String toString() {
        return type;
    }
}
