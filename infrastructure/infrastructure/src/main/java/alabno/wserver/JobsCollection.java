package alabno.wserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alabno.database.DatabaseConnection;
import alabno.exercise.Exercise;
import alabno.exercise.StudentCommit;
import alabno.exercise.StudentJob;

/**
 * Contains the known exercises in the system
 */
public class JobsCollection {

    private final Map<String, Exercise> allJobs = new HashMap<>();
    private WebSocketHandler webSocketHandler;
    private DatabaseConnection db;

    public JobsCollection(WebSocketHandler webSocketHandler, DatabaseConnection db) {
        this.webSocketHandler = webSocketHandler;
        this.db = db;
    }

    /**
     * @return the list of all job titles known
     */
    public List<String> getJobNames() {
        List<String> out = new ArrayList<>();
        out.addAll(allJobs.keySet());
        return out;
    }


    public void repopulate(DatabaseConnection db) {
        String sql = "SELECT\r\n  exercise_big_table.exname AS exname,\r\n  exercise.extype AS extype,\r\n  exercise_big_table.userindex AS userindex,\r\n  exercise_big_table.hash AS hash,\r\n  exercise_big_table.status AS status,\r\n  exercise_big_table.uname AS uname\r\nFROM\r\n  `exercise_big_table`\r\nJOIN\r\n  `exercise`\r\nON\r\n  exercise_big_table.exname = exercise.exname\r\nORDER BY\r\n  exercise.exname,\r\n  TIMESTAMP\r\nDESC";
        List<Map<String, Object>> results = db.retrieveQuery(sql);

        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String extype = (String) row.get("extype");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");
            String status = (String) row.get("status");
            String uname = (String) row.get("uname");
            
            update(exname, extype, uname, userindex, hash, status);
        }
    }

    public void update(String exname, String extype, String uname, String userindex, String hash, String status) {
        updateExercise(exname, extype);
        Exercise exercise = allJobs.get(exname);
        StudentJob studentJob = new StudentJob(uname, userindex, exercise, db);
        StudentCommit studentCommit = new StudentCommit(hash, studentJob, db, status);
        studentJob.addStudentCommit(studentCommit);
        exercise.addStudentJob(studentJob);
    }


    private void updateExercise(String exname, String extype) {
        if (!allJobs.containsKey(exname)) {
            Exercise exercise = new Exercise(exname, extype, db);
            allJobs.put(exname, exercise);
        }
    }


}
