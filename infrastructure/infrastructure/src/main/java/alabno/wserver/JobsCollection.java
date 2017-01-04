package alabno.wserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alabno.database.DatabaseConnection;
import alabno.exercise.StudentCommit;
import alabno.useraccount.UserAccount;

/**
 * Contains the known exercises in the system
 */
public class JobsCollection {

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
        String sql = "SELECT `exname` FROM `exercise`";
        List<Map<String, String>> results = db.retrieveQueryString(sql);
        for (Map<String, String> row : results) {
            String exname = row.get("exname");
            out.add(exname);
        }
        return out;
    }

    public void update(String exname, String extype, String uname, String userindex, String hash, String status) {
        StudentCommit studentCommit = new StudentCommit(exname, extype, uname, userindex, hash, status, db);
        studentCommit.updateDatabase();
    }

    public List<StudentCommit> getJobsOfStudent(UserAccount account) {
        String sql = "SELECT exercise_big_table.exname AS exname,\r\nexercise_big_table.uname AS uname,\r\nexercise_big_table.userindex AS userindex,\r\nexercise_big_table.hash AS hash,\r\nexercise_big_table.status AS status,\r\nexercise.extype AS extype\r\nFROM exercise_big_table JOIN exercise ON exercise_big_table.exname = exercise.exname\r\nWHERE exercise_big_table.uname = ?\r\nORDER BY exercise_big_table.timestamp DESC";
        String[] params = {account.getUsername()};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        if (results == null) {
            return null;
        }
        List<StudentCommit> out = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String uname = (String) row.get("uname");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");
            String status = (String) row.get("status");
            String extype = (String) row.get("extype");
            
            StudentCommit scommit = new StudentCommit(exname, extype, uname, userindex, hash, status, db);
            out.add(scommit);
        }
        return out;
    }

    public StudentCommit findJobLatest(String title, String studentNumber) {
        String sql =
                "SELECT exercise_big_table.exname AS exname,\r\nexercise_big_table.uname AS uname,\r\nexercise_big_table.userindex AS userindex,\r\nexercise_big_table.hash AS hash,\r\nexercise_big_table.status AS status,\r\nexercise.extype AS extype\r\nFROM exercise_big_table JOIN exercise ON exercise_big_table.exname = exercise.exname\r\nWHERE exercise_big_table.userindex = ? AND\r\nexercise_big_table.exname = ?\r\nORDER BY\r\nexercise_big_table.timestamp DESC";
        String[] params = {studentNumber, title};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        if (results.isEmpty()) {
            return null;
        }
        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String uname = (String) row.get("uname");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");
            String status = (String) row.get("status");
            String extype = (String) row.get("extype");
            return new StudentCommit(exname, extype, uname, userindex, hash, status, db);
        }
        return null;
    }

    public List<String> getStudentIdxsByTitle(String title) {
        List<String> out = new ArrayList<>();
        String sql =
                "SELECT `userindex` FROM `exercise_big_table` WHERE `exname` = ?";
        String[] params = {title};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        for (Map<String, Object> row : results) {
            String userindex = (String) row.get("userindex");
            out.add(userindex);
        }
        return out;
    }
    
    public List<StudentCommit> getStudentsByTitle(String title) {
        List<StudentCommit> out = new ArrayList<>();
        String sql =
                "SELECT exercise_big_table.exname AS exname,\r\nexercise_big_table.uname AS uname,\r\nexercise_big_table.userindex AS userindex,\r\nexercise_big_table.hash AS hash,\r\nexercise_big_table.status AS status,\r\nexercise.extype AS extype\r\nFROM exercise_big_table JOIN exercise ON exercise_big_table.exname = exercise.exname\r\nWHERE exercise_big_table.exname = ?";
        String[] params = {title};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String uname = (String) row.get("uname");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");
            String status = (String) row.get("status");
            String extype = (String) row.get("extype");
            out.add(new StudentCommit(exname, extype, uname, userindex, hash, status, db));
        }
        return out;
    }

    public StudentCommit findJob(String title, String studentNumber, String qhash) {
        String sql = 
                "SELECT exercise_big_table.exname AS exname,\r\nexercise_big_table.uname AS uname,\r\nexercise_big_table.userindex AS userindex,\r\nexercise_big_table.hash AS hash,\r\nexercise_big_table.status AS status,\r\nexercise.extype AS extype\r\nFROM exercise_big_table JOIN exercise ON exercise_big_table.exname = exercise.exname\r\nWHERE exercise_big_table.userindex = ? AND\r\nexercise_big_table.exname = ? AND\r\nexercise_big_table.hash = ?";
        String[] params = {studentNumber, title, qhash};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        if (results == null || results.isEmpty()) {
            return null;
        }
        for (Map<String, Object> row : results) {
            String exname = (String) row.get("exname");
            String uname = (String) row.get("uname");
            String userindex = (String) row.get("userindex");
            String hash = (String) row.get("hash");
            String status = (String) row.get("status");
            String extype = (String) row.get("extype");
            return new StudentCommit(exname, extype, uname, userindex, hash, status, db);
        }
        return null;
    }

    public List<String> getCommitsOfStudent(String title, String studentid) {
        String sql =
                "SELECT `hash` FROM `exercise_big_table` WHERE `exname` = ? AND `userindex` = ?";
        String[] params = {title, studentid};
        
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        List<String> out = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String hash = (String) row.get("hash");
            out.add(hash);
        }
        return out;
    }



}
