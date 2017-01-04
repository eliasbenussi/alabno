package alabno.executionunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;

import alabno.database.DatabaseConnection;
import alabno.utils.ConnUtils;
import alabno.utils.ConnUtils.Color;
import alabno.wserver.JobsCollection;

public class ExecutionUnitUpdate implements Runnable {

    private String title;
    private JobsCollection allJobs;
    private String studentIdx;
    private DatabaseConnection db;
    private WebSocket conn;

    public ExecutionUnitUpdate(String title, String studentIdx,
            JobsCollection allJobs, DatabaseConnection db, WebSocket conn) {
        this.title = title;
        this.allJobs = allJobs;
        this.studentIdx = studentIdx;
        this.db = db;
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            // Send message that job is being processed
            ConnUtils.sendStatusInfo(conn, "Checking for updates: " + title, Color.YELLOW, 10);

            boolean success = execute();
            
            if (success) {
                ConnUtils.sendStatusInfo(conn, "Update completed: " + title, Color.GREEN, 10);
            } else {
                ConnUtils.sendStatusInfo(conn, "Update failed: " + title, Color.RED, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Send message that job failed
            ConnUtils.sendStatusInfo(conn, "Update failed: " + title, Color.RED, 10);
        }
    }

    private boolean execute() {
        try {
            String updaterScriptPath = "infrastructure/updater.py";
            StringBuilder studentGitArguments = new StringBuilder();

            String exerciseType = findExerciseType();
            List<String> command = new ArrayList<>();
            command.addAll(Arrays.asList(
                    "python", 
                    updaterScriptPath, 
                    "--exname", 
                    title, 
                    "--extype", 
                    findExerciseType(), 
                    "--studentidx",
                    studentIdx
                    ));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            String lastLine = null;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                lastLine = line;
            }
            int code = process.waitFor();
            System.out.println("Return code was " + code + "\n");
            System.out.println("Last line was: " + lastLine);
            
            if (code == 1) {
                return false;
            } else if (code == 34) {
                // Send message that no update was found
                ConnUtils.sendStatusInfo(conn, "No updates found: " + title, Color.GREEN, 10);
                return true;
            }

            if (lastLine != null && lastLine.charAt(0) == '#') {
                String[] level1Split = lastLine.split("=");
                if (level1Split.length != 2) {
                    System.out.println("Invalid");
                    return false;
                }
                String s = level1Split[1];

                // split the hash
                // This split removes the final part containing
                // _out/postpro.json
                String firstpart = s.substring(0, s.length() - 17);
                // This part splits on slashes
                String[] slashsplit = firstpart.split("/");
                // now get the last piece of the split
                String befhash = slashsplit[slashsplit.length - 1];
                // and remove the words 'commit'
                String hash = befhash.substring(6, befhash.length());

                allJobs.update(title, exerciseType, findStudentUsername(), studentIdx, hash, "ok");
                return true;
            }

            return false;

        } catch (IOException e) {
            System.out.println("Subprocess encountered an error");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private String findExerciseType() {
        String sql = "SELECT `extype` FROM `exercise` WHERE `exname` = ?";
        String[] params = {title};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        for (Map<String, Object> row : results) {
            return (String) row.get("extype");
        }
        throw new RuntimeException("Could not find exercise type for exercise " + title);
    }

    private String findStudentUsername() {
        String sql = "SELECT DISTINCT `uname` FROM `exercise_big_table` WHERE `exname` = ? AND `userindex` = ?";
        String[] params = {title, studentIdx};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        for (Map<String, Object> row : results) {
            return (String) row.get("uname");
        }
        throw new RuntimeException("Could not find username for exercise " + title + " and student index " + studentIdx);
    }



}
