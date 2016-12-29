package alabno.wserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;

import alabno.database.DatabaseConnection;
import alabno.database.TransactionBuilder;
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
    private final Set<String> pendingJobs = new HashSet<>();
    private final Set<String> failedJobs = new HashSet<>();
    private WebSocketHandler webSocketHandler;
    private DatabaseConnection db;

    public JobsCollection(WebSocketHandler webSocketHandler, DatabaseConnection db) {
        this.webSocketHandler = webSocketHandler;
        this.db = db;
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
    public synchronized void addJob(String title, List<StudentJob> newJobs, WebSocket conn, List<String> gitList, List<String> unameList, List<String> hashList, String exerciseType) {

        recordJobsSucceeded(title, gitList, unameList, hashList, exerciseType);
        
        if (allJobs.containsKey(title)) {
            List<StudentJob> studentJobs = allJobs.get(title);
            if (studentJobs != null && !studentJobs.isEmpty()) {
                ConnUtils.sendAlert(conn, "A job with name " + title + " already exists. It will not be added");
                return;
            }
        }

        allJobs.put(title, newJobs);
        pendingJobs.remove(title);
        failedJobs.remove(title);

        webSocketHandler.broadcastJobList();
    }
    
    private void recordJobsSucceeded(String title, List<String> gitList, List<String> unameList, List<String> hashList, String exerciseType) {

        if (gitList.size() != hashList.size()) {
            throw new RuntimeException("Error: size of gitList is different from size of hashList: cloner.py missed something?");
        }
        
        TransactionBuilder tb = new TransactionBuilder();
        
        // update status to OK
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`, `status`) VALUES (?,?,?)";
        String[] params = {title, exerciseType, "ok"};
        tb.add(sql, params);
        
        for (int i = 0; i < gitList.size(); i++) {
            String uname = unameList.get(i);
            String userindex = "" + i;
            String hash = hashList.get(i);
            
            // insert entries in the bigtable
            sql = "REPLACE INTO `exercise_big_table`(`exname`, `uname`, `userindex`, `hash`) VALUES (?,?,?,?)";
            params = new String[] {title, uname, userindex, hash};
            tb.add(sql, params);
        }
        
        db.executeTransaction(tb);
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

    public void addJobPending(String title, String exerciseType) {
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`, `status`) VALUES (?,?,?)";
        String[] params = {title, exerciseType, "pending"};
        db.executeStatement(sql, params);
        
        allJobs.put(title, new ArrayList<StudentJob>());
        pendingJobs.add(title);
        webSocketHandler.broadcastJobList();
    }
    
    public boolean isPending(String title) {
        return pendingJobs.contains(title);
    }

    public void addFailedJob(String title, String exerciseType) {
        recordJobsFailed(title, exerciseType);
        
        allJobs.put(title, new ArrayList<StudentJob>());
        failedJobs.add(title);
        webSocketHandler.broadcastJobList();
    }
    
    public boolean isFailed(String title) {
        return failedJobs.contains(title);
    }
    
    private void recordJobsFailed(String title, String exerciseType) {
        // update status to FAIL
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`, `status`) VALUES (?,?,?)";
        String[] params = {title, exerciseType, "fail"};
        db.executeStatement(sql, params);
    }

}
