package alabno.wserver;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.msfeedback.FeedbackUpdaters;
import alabno.utils.ConnUtils;

public class WebSocketHandler {

    private SessionManager sessionManager = new SessionManager();
    private ExecutorService executor;
    private JobsCollection allJobs = new JobsCollection(this);
    private FeedbackUpdaters updaters;

    public WebSocketHandler(ExecutorService executor, FeedbackUpdaters updaters) {
        this.executor = executor;
        this.updaters = updaters;
    }

    public void handleMessage(WebSocket conn, String message) {

        // Parse the JSON
        JsonParser parser = new JsonParser(message);

        if (!parser.isOk()) {
            System.out.println("Json message received from client is malformed" + ". Ignoring...");
            return;
        }

        String type = parser.getString("type");
        if (type == null) {
            System.out.println("type is not set in the json message");
            return;
        }

        if (!checkCredential(parser, conn)) {
            ConnUtils.sendAlert(conn, "Please log in first");
            return;
        }

        switch (type) {
        case "login":
            handleLogin(parser, conn);
            break;
        case "new_assignment":
            handleNewAssignment(parser, conn);
            break;
        case "get_job":
            handleGetJob(parser, conn);
            break;
        case "retrieve_result":
            handleRetrieveResult(parser, conn);
            break;
        case "feedback":
            handleFeedback(parser, conn);
            break;
        default:
            System.out.println("Unrecognized client message type " + type);
        }
    }

    private void handleFeedback(JsonParser parser, WebSocket conn) {
        String source = parser.getString("source");
        String annType = parser.getString("ann_type");
        String annotation = parser.getString("annotation");
        this.updaters.updateAll(source, annType, annotation);
    }

    private void handleRetrieveResult(JsonParser parser, WebSocket conn) {

        String title = parser.getString("title");
        if (title == null || title.isEmpty()) {
            ConnUtils.sendAlert(conn, "retrieve_results: title can't be empty");
            return;
        }

        String student = parser.getString("student");
        if (student == null || student.isEmpty()) {
            ConnUtils.sendAlert(conn, "retrieve_results: student can't be empty");
            return;
        }
        int studentNumber;
        try {
            studentNumber = Integer.parseInt(student);
        } catch (NumberFormatException e) {
            ConnUtils.sendAlert(conn, "retrieve_results: could not parse student");
            return;
        }

        // get the object from memory
        List<StudentJob> group = allJobs.getJobGroupByTitle(title);
        if (group == null) {
            ConnUtils.sendAlert(conn, "retrieve_results: no group with name " + title);
            return;
        }

        if (studentNumber < 0 || studentNumber >= group.size()) {
            ConnUtils.sendAlert(conn, "retrieve_results: student id out of range");
            return;
        }

        StudentJob studentJob = group.get(studentNumber);

        // Read the JSON file
        String fileContent = studentJob.readPostProcessorOutput();

        // Prepare output message
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "postpro_result");
        msgobj.put("title", title);
        msgobj.put("student", student);
        msgobj.put("data", fileContent);
        conn.send(msgobj.toJSONString());
    }

    private void handleGetJob(JsonParser parser, WebSocket conn) {
        String title = parser.getString("title");
        if (title == null || title.isEmpty()) {
            ConnUtils.sendAlert(conn, "Received request for null title");
            return;
        }

        List<StudentJob> jobGroup = allJobs.getJobGroupByTitle(title);
        if (jobGroup == null) {
            ConnUtils.sendAlert(conn, "Couldn't find any job named " + title);
            return;
        }

        // generate job_group message
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_group");
        msgobj.put("title", title);
        JSONArray groupArray = new JSONArray();
        for (int i = 0; i < jobGroup.size(); i++) {
            groupArray.add("" + i);
        }
        msgobj.put("group", groupArray);
        conn.send(msgobj.toJSONString());
    }

    private boolean checkCredential(JsonParser parser, WebSocket conn) {
        if ("login".equals(parser.getString("type"))) {
            return true;
        }

        // Get the token
        String token = parser.getString("id");
        if (token == null) {
            return false;
        }

        // Get the corresponding connection
        WebSocket expected = sessionManager.getConnection(token);

        return conn == expected;
    }

    private void handleNewAssignment(JsonParser parser, WebSocket conn) {
        String title = parser.getString("title");
        String exerciseType = parser.getString("ex_type");
        String modelAnswerGitLink = parser.getString("model_git");
        JSONArray studentGitLinks = parser.getArray("students_git");

        if (title == null || title.isEmpty()) {
            ConnUtils.sendAlert(conn, "title is required");
            return;
        }

        if (exerciseType == null || exerciseType.isEmpty()) {
            ConnUtils.sendAlert(conn, "exercise type is required");
            return;
        }

        if (modelAnswerGitLink == null || modelAnswerGitLink.isEmpty()) {
            ConnUtils.sendAlert(conn, "model answer git repository required");
            return;
        }

        if (studentGitLinks == null) {
            ConnUtils.sendAlert(conn, "students repo git links required");
            return;
        } else {
            String msg = checkStudentGitLinks(studentGitLinks);
            if (msg != null) {
                ConnUtils.sendAlert(conn, msg);
                return;
            }
        }

        System.out.println("all checks passed");

        AssignmentCreator newAssignmentProcessor = new AssignmentCreator(title, exerciseType, modelAnswerGitLink,
                studentGitLinks, conn, allJobs);

        executor.submit(newAssignmentProcessor);

        // Send confirmation message
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_sent");
        conn.send(msgobj.toJSONString());
    }

    /**
     * @param studentGitLinks
     * @return a String containing the error message, or null if no error was
     *         detected
     */
    private String checkStudentGitLinks(JSONArray studentGitLinks) {
        String errormsg = "Malformed Student git link. It must be an HTTPS git repository";
        try {
            for (Object l : studentGitLinks) {
                String str = (String) l;
                if (!str.contains("https")) {
                    return errormsg;
                }
            }
        } catch (ClassCastException e) {
            return errormsg;
        }

        return null;
    }

    private void handleLogin(JsonParser parser, WebSocket conn) {
        boolean success = true;

        // get username
        String username = parser.getString("username");
        if (username == null || username.isEmpty()) {
            success = false;
        }

        // get password (hash)
        String password = parser.getString("password");
        if (password == null || password.isEmpty()) {
            success = false;
        }

        // check login
        // TODO check login

        String token = username + "-" + username.hashCode();

        // if login successful
        if (success) {
            JSONObject success_msg = new JSONObject();
            success_msg.put("type", "login_success");
            success_msg.put("id", "" + token);
            conn.send(success_msg.toJSONString());

            // Register in the session manager
            sessionManager.createSession(token, conn);

            // Send the currently existing jobs
            conn.send(getJobsListMessage().toJSONString());

            return;
        }

        // if login fails
        else {
            JSONObject failure_msg = new JSONObject();
            failure_msg.put("type", "login_fail");
            conn.send(failure_msg.toJSONString());
            return;
        }
    }

    /**
     * Send to all connected users the list of existing jobs. Used when new jobs
     * are submitted
     */
    public void broadcastJobList() {
        String msg = getJobsListMessage().toJSONString();

        sessionManager.broadcastMessage(msg);
    }

    private JSONObject getJobsListMessage() {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_list");
        JSONArray jobs = new JSONArray();
        for (String name : allJobs.getJobNames()) {
            jobs.add(name);
        }
        msgobj.put("jobs", jobs);
        return msgobj;
    }

    /**
     * Removes the connection from the active sessions
     * 
     * @param conn the connection that was closed
     */
    public void closeSession(WebSocket conn) {
        sessionManager.endSession(conn);
    }

}
