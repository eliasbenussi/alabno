package alabno.msfeedback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import alabno.database.MySqlDatabaseConnection;
import alabno.utils.FileUtils;

/**
 * Created by eb1314 on 08/11/16.
 */
public class HaskellMarkerUpdater implements MicroServiceUpdater {

    private static final String TRAINING_FILE_BASENAME = "simple-haskell-marker/training/train";

    private MySqlDatabaseConnection conn;
    private int currentNumbering = 0;

    public HaskellMarkerUpdater(MySqlDatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
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
        }
        
        catFile.close();
        
        // TODO train a Column Data Classifier, and serialize it
        
        // rename to effective .bin
        try {
            FileUtils.rename(getCurrentTemporarySerialName(), getCurrentSerializedName());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // documentation/feedback_haskell_marker.txt has more details about the formats
    }

    @Override
    public void update(String source, String type, String annotation) {
        String queryCategories = "INSERT INTO ... () VALUES ()";
        String[] parametersCategories = {createNewName(), type, annotation};

        String queryTraining = "INSERT INTO ... () VALUES ()";
        String[] parametersTraining = {createNewName(), source};

        conn.executeStatement(queryCategories, parametersCategories);
        conn.executeStatement(queryTraining, parametersTraining);
    }

    private String createNewName() {
        // TODO make function that creates valid new name if annotation does not exist already
        return "Abracao";
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
