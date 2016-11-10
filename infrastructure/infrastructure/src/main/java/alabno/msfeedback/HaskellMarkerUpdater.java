package alabno.msfeedback;

import alabno.database.MySqlDatabaseConnection;

/**
 * Created by eb1314 on 08/11/16.
 */
public class HaskellMarkerUpdater implements MicroServiceUpdater {

    private MySqlDatabaseConnection conn;

    public HaskellMarkerUpdater(MySqlDatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
        // Read from database all entries, dump them to a temporary text file,
        // create a classifier, and then dump it out to disk
        
        // documentation/feedback_haskell_marker.txt has more details about the formats
    }

    @Override
    public void update(String source, String type, String annotation) {
        conn.executeQuery("INSERT INTO ... () VALUES ()");
    }
}
