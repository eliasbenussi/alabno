package alabno.wserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.database.DatabaseConnection;
import alabno.database.TransactionBuilder;

public class AssignmentCreator implements Runnable {

    private String title;
    private String exerciseType;
    private String modelAnswerGitLink;
    private JSONArray studentGitLinks;
    private WebSocket conn;
    private JobsCollection allJobs;
    private DatabaseConnection dbconn;

    public AssignmentCreator(String title, String exerciseType, String modelAnswer, JSONArray studentGitLinks,
            WebSocket conn, JobsCollection allJobs, DatabaseConnection dbconn) {
        this.title = title;
        this.exerciseType = exerciseType;
        this.modelAnswerGitLink = modelAnswer;
        this.studentGitLinks = studentGitLinks;
        this.conn = conn;
        this.allJobs = allJobs;
        this.dbconn = dbconn;
    }

    @Override
    public void run() {
        try {
            String clonerScriptPath = "infrastructure/cloner.py";
            StringBuilder studentGitArguments = new StringBuilder();

            List<String> gitList = new ArrayList<>();
            List<String> unameList = new ArrayList<>();
            List<String> hashList = new ArrayList<>();
            
            for (Object o : studentGitLinks) {
                JSONObject gitobj = (JSONObject) o;
                String gitlink = (String) gitobj.get("git");
                String uname = (String) gitobj.get("uname");
                gitList.add(gitlink);
                unameList.add(uname);
            }
            
            for (String g : gitList) {
                studentGitArguments.append(g + " ");
            }

            List<String> command = new ArrayList<>();
            command.addAll(Arrays.asList("python", clonerScriptPath, "--exname", title, "--extype", exerciseType, "--students",
                    studentGitArguments.toString()));

            if (modelAnswerGitLink != null) {
                command.add("--model");
                command.add(modelAnswerGitLink);
            }

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

            if (lastLine != null && lastLine.charAt(0) == '#') {
                String[] level1Split = lastLine.split("=");
                if (level1Split.length != 2) {
                    System.out.println("Invalid");
                    return;
                }
                String outputs = level1Split[1];
                String[] level2Split = outputs.split(" ");

                // Add the job to the JobsCollection
                List<StudentJob> newJob = new ArrayList<>();
                for (String s : level2Split) {
                    StudentJob aStudentJob = new StudentJob(s, exerciseType);
                    newJob.add(aStudentJob);
                    
                    // split the hash
                    // This split removes the final part containing _out/postpro.json
                    String firstpart = s.substring(0, s.length() - 17);
                    // This part splits on slashes
                    String[] slashsplit = firstpart.split("/");
                    // now get the last piece of the split
                    String befhash = slashsplit[slashsplit.length - 1];
                    // and remove the words 'commit'
                    String hash = befhash.substring(6, befhash.length());
                    
                    hashList.add(hash);
                }
                allJobs.addJob(title, newJob, conn);
                
                recordJobsSucceeded(title, gitList, unameList, hashList);
                return;
            }

        } catch (IOException e) {
            System.out.println("Subprocess encountered an error");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        recordJobsFailed(title);

    }

    private void recordJobsSucceeded(String title, List<String> gitList, List<String> unameList, List<String> hashList) {

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
        
        dbconn.executeTransaction(tb);
    }

    private void recordJobsFailed(String title2) {
        // update status to FAIL
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`, `status`) VALUES (?,?,?)";
        String[] params = {title, exerciseType, "fail"};
        dbconn.executeStatement(sql, params);
    }
}
