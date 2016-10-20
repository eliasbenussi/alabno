package postprocessor;

enum ErrorType {
    SYNTAX("syntax", 10.0),
    SEMANTIC("semantic", 1.0),
    STYLE("style", 0.1);

    private final String type;
    private final double deductedMarks;

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

    /**
     * Takes string indicating error type (e.g. as retrieved from JSON files)
     * and returns corresponding ErrorType.
     * @param jsonErrorType
     * @return Returns an instance of an ErrorType
     */
    public static ErrorType convertStringToErrorType(String jsonErrorType) {
        switch(jsonErrorType) {
            case "syntax":
                return ErrorType.SYNTAX;
            case "semantic":
                return ErrorType.SEMANTIC;
            case "style":
                return ErrorType.STYLE;
            default:
                return null;
        }
    }
}
