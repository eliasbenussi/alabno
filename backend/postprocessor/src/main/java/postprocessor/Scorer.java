package postprocessor;

import java.util.Iterator;
import java.util.Map;

public class Scorer {

    private String finalScore;
    private Double numberGrade;
    private Map<String, Double> microServiceScores;

    public Scorer(Map<String, Double> microServiceScores) {
        this.microServiceScores = microServiceScores;
        this.numberGrade = 0.0;
        applyMeanMicroServiceGrading();
    }

    public String getScore() {
        return finalScore;
    }

    public String getLetterGrade() {
        if (numberGrade >= 90) {
            return "A*";
        } else if (numberGrade >= 80) {
            return "A+";
        } else if (numberGrade >= 70) {
            return "A";
        } else if (numberGrade >= 60) {
            return "B";
        } else if (numberGrade >= 50) {
            return "C";
        } else if (numberGrade >= 40) {
            return "D";
        } else {
            return "E";
        }
    }

    private void applyMeanMicroServiceGrading() {
        Iterator<Double> iterator = microServiceScores.values().iterator();
        double addedScore = 0.0;
        while (iterator.hasNext()) {
            addedScore += iterator.next();
        }
        numberGrade = addedScore / microServiceScores.size();
        updateFinalGrade();
    }

    private void updateFinalGrade() {
        finalScore = "\"number_grade\": \"" + numberGrade + "/100\",\"letter_grade\": \"" + getLetterGrade() + "\"";
    }
}
