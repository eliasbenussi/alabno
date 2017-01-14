package alabno.msfeedback.overblockupdater;

import alabno.database.DatabaseConnection;
import alabno.msfeedback.Mark;
import alabno.msfeedback.MicroServiceUpdater;
import alabno.msfeedback.Runner;
import alabno.utils.FileUtils;
import alabno.utils.StringUtils;
import alabno.utils.SubprocessUtils;
import alabno.wserver.SourceDocument;

import java.io.*;
import java.util.*;

public class OverBlockMarkerUpdater implements MicroServiceUpdater {

    private static final String TRAINING_FILE_BASENAME = "backend/overlapping-block-marker/training/train";

    private DatabaseConnection conn;
    private static final String OBMPath = FileUtils.getWorkDir() + "/backend/overlapping-block-marker/";

    private int currentNumbering = 0;

    // Holds a map from annotation to identifiers, which allows to re-use some identifiers for similar annotations
    private Map<String, String> existingAnnotationsToIdentifiers = new HashMap<>();

    public OverBlockMarkerUpdater(DatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
        System.out.println("OverBlockMarkerUpdater:init()");

        // create the directory
        SubprocessUtils.call("rm -rf " + OBMPath + "training");
        SubprocessUtils.call("mkdir " + OBMPath + "training");

        // Start updater thread
        Thread updaterThread = new Thread(new Runner(this, "OverlappingBlockMarker"));
        updaterThread.start();
    }


    @Override
    public void update(SourceDocument document, int lineNumber, String type, String annotation) {
        String source = getLineContent(document.getPath(), lineNumber);
        if (source == null || source.isEmpty()) {
            return;
        }
        System.out.println("OverBlockMarkerUpdater:update(" + type + "," + annotation + ")");
        CategoryName newName = createNewName(annotation);
        // add it to the map
        existingAnnotationsToIdentifiers.put(annotation, newName.name);

        // format source, type and annotation
        source = source.replace("\n", "\\n").replace("\t", "\\t");
        source = source.trim();
        if (source.isEmpty()) {
            return;
        }
        type = type.replace("\n", "\\n").replace("\t", "\\t");
        annotation = annotation.replace("\n", "\\n").replace("\t", "\\t");
        annotation = annotation.trim();
        if (annotation.isEmpty()) {
            return;
        }

        String queryCategories = "INSERT INTO OverBlockCategories (name, type, annotation) VALUES (?, ?, ?)";
        String[] parametersCategories = {newName.name, type, annotation};

        String queryTraining = "INSERT INTO OverBlockTraining (name, text) VALUES (?, ?)";
        String[] parametersTraining = {newName.name, source};

        if (newName.insert) {
            conn.executeStatement(queryCategories, parametersCategories);
        }
        conn.executeStatement(queryTraining, parametersTraining);

        // update Training set done by the runner
    }

    private String getLineContent(String path, int lineNumber) {
        String python_script_path = "backend/overlapping-block-marker/split_for_updater.py";
        List<String> command = new ArrayList<>();
        command.addAll(Arrays.asList(
                "python",
                python_script_path,
                path,
                String.valueOf(lineNumber)
        ));
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = input.readLine()) != null) {
                builder.append(line);
            }
            input.close();
            return builder.toString();
        } catch (IOException e) {
            System.out.print("Error executing " + python_script_path + ".");
            e.printStackTrace();
            return null;
        }
    }

    public void updateTraining() {
        System.out.println("OverBlockMarkerUpdater:updateTraining()");

        // Read from database all entries, dump them to a temporary text file,
        // create a classifier, and then dump it out to disk
        String sql = "SELECT * FROM `OverBlockTraining`";

        List<Map<String, String>> tuples = conn.retrieveQueryString(sql);

        // increase numbering
        currentNumbering++;
        String trainingName = getCurrentTrainingName();

        // Clear directory from very old entires
        String cmd = "python " + OBMPath + "IncrementSerializedClassifier.py --clean";
        SubprocessUtils.call(cmd);

        PrintWriter outfile = null;
        try {
            outfile = new PrintWriter(trainingName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // write the results of the database down to file
        for (Map<String, String> tuple : tuples) {
            String name = tuple.get("name");
            String text = tuple.get("text");
            if (name == null || text == null || name.trim().isEmpty() || text.trim().isEmpty()) {
                continue;
            }
            outfile.println(name + "\t" + text);
        }

        outfile.close();

        // Retrieve the content of the categories map
        sql = "SELECT * FROM `OverBlockCategories`";
        List<Map<String, String>> cats = conn.retrieveQueryString(sql);

        // Write categories to categories file

        PrintWriter catFile = null;
        try {
            catFile = new PrintWriter(getCurrentCategoriesName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (Map<String, String> cat : cats) {
            String name = cat.get("name");
            String type = cat.get("type");
            String annotation = cat.get("annotation");
            if (name == null || type == null || annotation == null || name.trim().isEmpty() || type.trim().isEmpty()
                    || annotation.trim().isEmpty()) {
                continue;
            }
            catFile.println(name + "\t" + type + "\t" + annotation);
            existingAnnotationsToIdentifiers.put(annotation, name);
        }

        catFile.close();

        // after writing the serialized file, call alabno/backend/overlapping-block-marker/IncrementSerializedClassifier.py
        // Which will take care of creating/updating the manifest.txt file for the microservice
        cmd = "python " + OBMPath + "IncrementSerializedClassifier.py";
        SubprocessUtils.call(cmd);

    }

    private class CategoryName {
        boolean insert; // True if a database insertion is required
        String name;

        CategoryName(boolean insert, String name) {
            this.insert = insert;
            this.name = name;
        }
    }

    /**
     * @param desiredAnnotation the desired annotation string to be used
     * @return the new name for the annotation, or the existing one if an annotation with very similar text was already available.
     */
    private CategoryName createNewName(String desiredAnnotation) {
        int maxAllowedDistance =
                (int) Math.floor(0.1d * desiredAnnotation.length());

        int minFoundDistance = maxAllowedDistance + 1;
        String minDistanceAnnotation = null;

        // Compute the string with minimal distance with the desired one
        for (String oldAnnotation : existingAnnotationsToIdentifiers.keySet()) {
            int currentDistance = StringUtils.computeLevenshteinDistance(desiredAnnotation.toLowerCase(), oldAnnotation.toLowerCase());
            if (currentDistance < minFoundDistance) {
                minFoundDistance = currentDistance;
                minDistanceAnnotation = oldAnnotation;
            }
        }

        // If the minimum distance annotation is within threshold, return the same
        // category name
        if (minFoundDistance <= maxAllowedDistance) {
            return new OverBlockMarkerUpdater.CategoryName(false, existingAnnotationsToIdentifiers.get(minDistanceAnnotation));
        }

        // Otherwise, create a new name, every time checking that the database doesn't have it already
        int retryAttempts = 100;
        for (int i = 0; i < retryAttempts; i++) {
            String newName = "ovb" + StringUtils.randomAsciiStringNumerical(10);

            // Test that the database doesn't have it already
            String query = "SELECT `name`, `type`, `annotation` FROM `OverBlockCategories` WHERE `name` = ?";
            String[] parameters = {newName};
            List<Map<String, Object>> results = conn.retrieveStatement(query, parameters);
            if (results.size() == 0) {
                return new OverBlockMarkerUpdater.CategoryName(true, newName);
            }
        }

        return new OverBlockMarkerUpdater.CategoryName(false, "invalid");
    }

    private String getCurrentFilename(String ext) {
        String numbers = String.format("%05d", currentNumbering);
        return TRAINING_FILE_BASENAME + numbers + ext;
    }

    private String getCurrentTrainingName() {
        return getCurrentFilename(".train");
    }

    private String getCurrentSerializedName() {
        return getCurrentFilename(".bin");
    }

    private String getCurrentCategoriesName() {
        return getCurrentFilename(".csv");
    }

    @Override
    public void updateMark(SourceDocument source, String exerciseType, Mark mark) {
        // Not handling marking changes for the moment
    }
}
