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
    System.out.println("Abracao");
    }

    @Override
    public void update(String source, String type, String annotation) {
        conn.executeQuery("INSERT INTO ... () VALUES ()");
    }
}
