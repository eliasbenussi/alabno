package javamarker;

import javafx.util.Pair;
import utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaClassifier implements ScriptClassifier {

    private String trainingSetPath;
    List<Pair<String, List<Integer>>> trainingSet;

    public JavaClassifier(Arguments arguments) {
        this.trainingSetPath = arguments.getTrainingDataPath();
        init();
    }

    private void init() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(trainingSetPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        trainingSet = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] components = line.split("\\t");
            trainingSet.add(new Pair<>(components[1], StringUtils.formatLine(components[1])));
        }
        scanner.close();
    }

    @Override
    public void classify(JavaSplitDocument document) {

        List<String> trainingSamples = new ArrayList<>();



    }
}
