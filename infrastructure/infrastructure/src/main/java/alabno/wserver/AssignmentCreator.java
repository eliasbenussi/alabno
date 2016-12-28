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

import alabno.utils.ConnUtils;
import alabno.utils.FileUtils;

public class AssignmentCreator implements Runnable {

    private String title;
    private String exerciseType;
    private String modelAnswerGitLink;
    private JSONArray studentGitLinks;
    private WebSocket conn;
    private JobsCollection allJobs;

    public AssignmentCreator(String title, String exerciseType, String modelAnswer, JSONArray studentGitLinks,
            WebSocket conn, JobsCollection allJobs) {
        this.title = title;
        this.exerciseType = exerciseType;
        this.modelAnswerGitLink = modelAnswer;
        this.studentGitLinks = studentGitLinks;
        this.conn = conn;
        this.allJobs = allJobs;
    }

    @Override
    public void run() {
        try {
            String clonerScriptPath = "infrastructure/cloner.py";
            StringBuilder studentGitArguments = new StringBuilder();

            for (Object o : studentGitLinks) {
                studentGitArguments.append(o + " ");
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
                }
                allJobs.addJob(title, newJob, conn);
            }

        } catch (IOException e) {
            System.out.println("Subprocess encountered an error");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
