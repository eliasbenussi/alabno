package alabno.msfeedback;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alabno.database.MySqlDatabaseConnection;
import alabno.utils.FileUtils;
import alabno.utils.StringUtils;
import alabno.utils.SubprocessUtils;

/**
 * Created by eb1314 on 08/11/16.
 */
public class HaskellMarkerUpdater implements MicroServiceUpdater {

    private static final String TRAINING_FILE_BASENAME = "simple-haskell-marker/training/train";

    private MySqlDatabaseConnection conn;
    private int currentNumbering = 0;
    
    // Holds a map from annotation to identifiers, which allows to re-use some identifiers for similar annotations
    private Map<String, String> existingAnnotationsToIdentifiers = new HashMap<>();

    public HaskellMarkerUpdater(MySqlDatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
        System.out.println("HaskellMarkerUpdater:init()");
        
        // create the directory
        SubprocessUtils.call("mkdir " + FileUtils.getWorkDir() + "/simple-haskell-marker/training");
        
        updateTraining();
    }

    @Override
    public void update(String source, String type, String annotation) {
        String queryCategories = "INSERT INTO HaskellCategories (name, type, annotation) VALUES (?, ?, ?)";
        String[] parametersCategories = {createNewName(annotation), type, annotation};

        String queryTraining = "INSERT INTO HaskellTraining (name, text) VALUES (?, ?)";
        String[] parametersTraining = {createNewName(annotation), source};

        conn.executeStatement(queryCategories, parametersCategories);
        conn.executeStatement(queryTraining, parametersTraining);
        
        // TODO adapt this to use createNewName properly
    }
    
    public void updateTraining() {
        System.out.println("HaskellMarkerUpdater:updateTraining()");
        
        // Read from database all entries, dump them to a temporary text file,
        // create a classifier, and then dump it out to disk
        String sql = "SELECT * FROM `HaskellTraining`";

        List<Map<String, String>> tuples = conn.retrieveQueryString(sql);

        // increase numbering
        currentNumbering++;
        String trainingName = getCurrentTrainingName();

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
            catFile.println(name + "\t" + type + "\t" + annotation);
            existingAnnotationsToIdentifiers.put(annotation, name);
        }

        catFile.close();

        // TODO train a Column Data Classifier, and serialize it
        // TODO please name the serialized output file using getCurrentSerializedName()
        
        // TODO after writing the serialized file, call alabno/simple-haskell-marker/IncrementSerializedClassifier.py
        // Which will take care of creating/updating the manifest.txt file for the microservice
        String alabnoDirectory = FileUtils.getWorkDir();
        String cmd = "python " + alabnoDirectory + "/simple-haskell-marker/IncrementSerializedClassifier.py";
        SubprocessUtils.call(cmd);

        // documentation/feedback_haskell_marker.txt has more details about the formats. PLEASE refer to that
    }

    /**
     * @param desiredAnnotation the desired annotation string to be used
     * @return 
     */
    private String createNewName(String desiredAnnotation) {
        int maxAllowedDistance = (int) Math.round(0.05d * desiredAnnotation.length());
        
        int minFoundDistance = maxAllowedDistance + 1;
        String minDistanceAnnotation = null;
        
        // Compute the string with minimal distance with the desired one
        for (String oldAnnotation : existingAnnotationsToIdentifiers.keySet()) {
            int currentDistance = StringUtils.computeLevenshteinDistance(desiredAnnotation, oldAnnotation);
            if (currentDistance < minFoundDistance) {
                minFoundDistance = currentDistance;
                minDistanceAnnotation = oldAnnotation;
            }
        }
        
        // If the minimum distance annotation is within threshold, return the same 
        // category name
        
        // Otherwise, create a new name, every time checking that the database doesn't have it already
        
        // TODO complete this implementation
        
        return "ok";
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
    
    private String getCurrentTemporarySerialName() {
        return getCurrentFilename(".bin.tmp");
    }
    
    private String getCurrentCategoriesName() {
        return getCurrentFilename(".csv");
    }
    
    

}
