package postprocessor;

public class Scorer {

    private String finalScore;
    private int numberGrade;

    public Scorer(String jsonAggregatedErrors) {

    }

    public String getScore() {
        return finalScore;
    }

    private int getNumberGrade() {
        return numberGrade;
    }

    private String getLetterGrade() {
        return "E";
    }

    private void getMeanScoreFromMicroServices() {

    }
}
