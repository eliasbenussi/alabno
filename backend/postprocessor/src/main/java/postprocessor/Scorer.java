package postprocessor;

import json_parser.MicroServiceOutput;

import java.util.List;
import java.util.stream.Collectors;

class Scorer {

    private List<MicroServiceOutput> microServiceOutputs;
    private String letterGrade;
    private double numberGrade;

    Scorer(List<MicroServiceOutput> microServiceOutputs) {
        this.microServiceOutputs = microServiceOutputs;
        applyMeanMicroServiceGrading();
    }

    String getLetterGrade() {
        return letterGrade;
    }

    double getNumberGrade() {
        return numberGrade;
    }

    private String convertToLetterGrade(Double numberGrade) {
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

    private void applyMeanMicroServiceGrading() {
        List<Double> scores = getScoresFromMicroServiceOutputs();
        double addedScore = scores.stream().reduce(0.0, (a, b) -> a + b);
        numberGrade = addedScore / microServiceOutputs.size();
        letterGrade = convertToLetterGrade(numberGrade);
    }

}
