package postprocessor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class Scorer {

    private JSONArray finalScore = new JSONArray();
    private Double numberGrade;
    private Map<String, Double> microServiceScores;

    public Scorer(Map<String, Double> microServiceScores) {
        this.microServiceScores = microServiceScores;
        this.numberGrade = 0.0;
        applyMeanMicroServiceGrading();
    }

    public JSONArray getScore() {
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
        JSONObject numberGradeJSON = new JSONObject();
        numberGradeJSON.put("number", numberGrade);
        JSONObject letterGradeJSON = new JSONObject();
        letterGradeJSON.put("letter", getLetterGrade());
        finalScore.add(numberGradeJSON);
        finalScore.add(letterGradeJSON);
    }
}
