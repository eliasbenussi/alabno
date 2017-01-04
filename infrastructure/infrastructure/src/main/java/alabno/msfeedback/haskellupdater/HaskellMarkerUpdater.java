package alabno.msfeedback.haskellupdater;

import alabno.database.DatabaseConnection;
import alabno.msfeedback.Mark;
import alabno.msfeedback.MicroServiceUpdater;
import alabno.msfeedback.Runner;
import alabno.simple_haskell_marker.HaskellSplitter;
import alabno.utils.FileUtils;
import alabno.utils.StringUtils;
import alabno.utils.SubprocessUtils;
import alabno.wserver.SourceDocument;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HaskellMarkerUpdater implements MicroServiceUpdater {

    private static final String TRAINING_FILE_BASENAME = "backend/simple-haskell-marker/training/train";

    private DatabaseConnection conn;
    private static final String SHMPath = FileUtils.getWorkDir() + "/backend/simple-haskell-marker/";

    private int currentNumbering = 0;

    // Holds a map from annotation to identifiers, which allows to re-use some identifiers for similar annotations
    private Map<String, String> existingAnnotationsToIdentifiers = new HashMap<>();

    public HaskellMarkerUpdater(DatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
        System.out.println("HaskellMarkerUpdater:init()");

        // create the directory
        SubprocessUtils.call("rm -rf " + SHMPath + "training");
        SubprocessUtils.call("mkdir " + SHMPath + "training");

        // Start updater thread
        Thread updaterThread = new Thread(new Runner(this, "HaskellMarker"));
        updaterThread.start();
    }


    @Override
    public synchronized void update(SourceDocument doc, int lineNumber, String type, String annotation) {
        HaskellSplitter haskellSplitter = new HaskellSplitter(doc.getAllLines());
        String source = haskellSplitter.getBlockTextAt(lineNumber);
        if (source == null || source.isEmpty()) {
            return;
        }

        System.out.println("HaskellMarkerUpdater:update(" + type + "," + annotation + ")");
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

        String queryCategories = "INSERT INTO HaskellCategories (name, type, annotation) VALUES (?, ?, ?)";
        String[] parametersCategories = {newName.name, type, annotation};

        String queryTraining = "INSERT INTO HaskellTraining (name, text) VALUES (?, ?)";
        String[] parametersTraining = {newName.name, source};

        if (newName.insert) {
            conn.executeStatement(queryCategories, parametersCategories);
        }
        conn.executeStatement(queryTraining, parametersTraining);

        // update Training set is done by the Runner
    }

    public synchronized void updateTraining() {
        System.out.println("HaskellMarkerUpdater:updateTraining()");

        // Read from database all entries, dump them to a temporary text file,
        // create a classifier, and then dump it out to disk
        String sql = "SELECT * FROM `HaskellTraining`";

        List<Map<String, String>> tuples = conn.retrieveQueryString(sql);

        // increase numbering
        currentNumbering++;
        String trainingName = getCurrentTrainingName();

        // Clear directory from very old entires
        String cmd = "python " + SHMPath + "IncrementSerializedClassifier.py --clean";
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
        sql = "SELECT * FROM `HaskellCategories`";
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

        // after writing the serialized file, call alabno/backend/simple-haskell-marker/IncrementSerializedClassifier.py
        // Which will take care of creating/updating the manifest.txt file for the microservice
        cmd = "python " + SHMPath + "IncrementSerializedClassifier.py";
        SubprocessUtils.call(cmd);

        // documentation/feedback_haskell_marker.txt has more details about the formats. PLEASE refer to that
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
            return new CategoryName(false, existingAnnotationsToIdentifiers.get(minDistanceAnnotation));
        }

        // Otherwise, create a new name, every time checking that the database doesn't have it already
        int retryAttempts = 100;
        for (int i = 0; i < retryAttempts; i++) {
            String newName = "hs" + StringUtils.randomAsciiStringNumerical(10);

            // Test that the database doesn't have it already
            String query = "SELECT `name`, `type`, `annotation` FROM `HaskellCategories` WHERE `name` = ?";
            String[] parameters = {newName};
            List<Map<String, Object>> results = conn.retrieveStatement(query, parameters);
            if (results.size() == 0) {
                return new CategoryName(true, newName);
            }
        }

        return new CategoryName(false, "invalid");
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
        // The Haskell Marker Updater does not need to handle marking changes
    }


}
