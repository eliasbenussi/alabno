package postprocessor;

import json_parser.MicroServiceOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Scorer {

    private List<MicroServiceOutput> microServiceOutputs;
    private String letterGrade;
    private double numberGrade;

    public Scorer(List<MicroServiceOutput> microServiceOutputs) {
        this.microServiceOutputs = microServiceOutputs;
        applyMeanMicroServiceGrading();
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public double getScore() {
        return numberGrade;
    }

    public String getLetterGrade(Double numberGrade) {
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
        } else if (numberGrade >= 30) {
            return "E";
        } else {
            return "F";
        }
    }

    private List<Double> getScoresFromMicroServiceOutputs() {
        return microServiceOutputs.stream().map(MicroServiceOutput::getScore).collect(Collectors.toList());
    }

    public void applyMeanMicroServiceGrading() {
        List<Double> scores = getScoresFromMicroServiceOutputs();
        double addedScore = scores.stream().reduce(0.0, (a, b) -> a + b);
        numberGrade = addedScore / microServiceOutputs.size();
        letterGrade = getLetterGrade(numberGrade);
    }

}
