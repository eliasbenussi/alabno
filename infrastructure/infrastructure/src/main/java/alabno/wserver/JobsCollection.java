package alabno.wserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;

import alabno.database.DatabaseConnection;
import alabno.utils.ConnUtils;
import alabno.utils.FileUtils;

/**
 * Contains the known CompositeJobs in the system
 */
public class JobsCollection {

    // TODO allow to have states with jobs (ok, in progress, error)
    // and to update these states
    // TODO database supported jobs collection

    private final Map<String, List<StudentJob>> allJobs = new HashMap<>();
    private WebSocketHandler webSocketHandler;

    public JobsCollection(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * adds the job to the repository of all executed marking jobs
     * 
     * @param title
     *            the name of the job to be added
     * @param newJobs
     *            list of student that did the exercise
     * @param conn
     *            client connection who requested it
     */
    public synchronized void addJob(String title, List<StudentJob> newJobs, WebSocket conn) {

        if (allJobs.containsKey(title)) {
            ConnUtils.sendAlert(conn, "A job with name " + title + " already exists. It will not be added");
            return;
        }

        allJobs.put(title, newJobs);

        webSocketHandler.broadcastJobList();
    }

    /**
     * @return the list of all job titles known
     */
    public List<String> getJobNames() {
        List<String> out = new ArrayList<>();
        out.addAll(allJobs.keySet());
        return out;
    }

    /**
     * @param title
     *            the title of the job to be found
     * @return the list of student jobs with corresponding title
     */
    public List<StudentJob> getJobGroupByTitle(String title) {
        return allJobs.get(title);
    }

    public void repopulate(DatabaseConnection db) {
        String sql = "SELECT\r\n  exercise_big_table.exname AS exname,\r\n  exercise.extype AS extype,\r\n  exercise_big_table.userindex AS userindex,\r\n  exercise_big_table.hash AS hash\r\nFROM\r\n  `exercise_big_table`\r\nJOIN\r\n  `exercise`\r\nON\r\n  exercise_big_table.exname = exercise.exname\r\nORDER BY\r\n  exercise.exname,\r\n  TIMESTAMP\r\nDESC";
        List<Map<String, Object>> results = db.retrieveQuery(sql);

        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String extype = (String) row.get("extype");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");

            String jsonLocation = generateJsonLocation(exname, userindex, hash);
            if (jsonLocation == null) {
                continue;
            }

            StudentJob studentJob = new StudentJob(jsonLocation, extype);

            if (!allJobs.containsKey(exname)) {
                allJobs.put(exname, new ArrayList<StudentJob>());
            }
            allJobs.get(exname).add(studentJob);
        }
    }

    private String generateJsonLocation(String exname, String userindex, String hash) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.getWorkDir());
        sb.append("/");
        sb.append("tmp");
        sb.append("/");
        sb.append(exname);
        sb.append("/");
        sb.append("student" + userindex);
        sb.append("/");
        sb.append("commit" + hash + "_out");
        sb.append("/");
        sb.append("postpro.json");

        String postproPath = sb.toString();
        System.out.println("Generated a json location from DB: " + postproPath);

        File f = new File(postproPath);
        if (f.exists() && !f.isDirectory()) {
            return postproPath;
        } else {
            return null;
        }
    }

}
