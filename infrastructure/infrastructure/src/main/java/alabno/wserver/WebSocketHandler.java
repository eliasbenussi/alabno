package alabno.wserver;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.database.MySqlDatabaseConnection;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.msfeedback.Mark;
import alabno.useraccount.UserAccount;
import alabno.useraccount.UserType;
import alabno.userauth.Authenticator;
import alabno.userauth.TokenGenerator;
import alabno.usercapabilities.Permissions;
import alabno.userstate.ActiveSessions;
import alabno.userstate.UserSession;
import alabno.userstate.UserState;
import alabno.utils.ConnUtils;
import alabno.utils.FileUtils;

public class WebSocketHandler {

    private ActiveSessions sessionManager = new ActiveSessions();
    private ExecutorService executor;
    private JobsCollection allJobs = new JobsCollection(this);
    private FeedbackUpdaters updaters;
    private MySqlDatabaseConnection db;
    private Authenticator authenticator;
	private TokenGenerator tokenGenerator;
	
	private Set<String> uncheckedCredentialsMessages = new HashSet<>();
    private Permissions permissions;

    public WebSocketHandler(ExecutorService executor, FeedbackUpdaters updaters, MySqlDatabaseConnection db, 
            Authenticator authenticator, TokenGenerator tokenGenerator, Permissions permissions) {
        this.executor = executor;
        this.updaters = updaters;
        this.db = db;
        this.authenticator = authenticator;
        this.tokenGenerator = tokenGenerator;
        this.permissions = permissions;
        
        uncheckedCredentialsMessages.add("validatetoken");
        uncheckedCredentialsMessages.add("login");
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
        
        if (!isPermitted(parser, conn, type)) {
            ConnUtils.sendAlert(conn, "You are not allowed to perform this action: " + type);
            return;
        }

        switch (type) {
        case "login":
            handleLogin(parser, conn);
            break;
        case "validatetoken":
        	handleValidateToken(parser, conn);
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
        case "markfeedback":
            handleMarkFeedback(parser, conn);
            break;
        default:
            System.out.println("Unrecognized client message type " + type);
        }
    }

    private boolean isPermitted(JsonParser parser, WebSocket conn, String action) {
        if (uncheckedCredentialsMessages.contains(action)) {
            return true;
        }
        String token = parser.getString("id");
        UserSession session = sessionManager.getSession(token);
        UserAccount account = session.getAccount();
        UserType userType = account.getUserType();
        return permissions.canPerform(userType, action);
    }

    private void handleValidateToken(JsonParser parser, WebSocket conn) {
		String username = parser.getString("username");
		String token = parser.getString("token");
		
		if (username == null || username.isEmpty() || token == null || token.isEmpty()) {
			return;
		}
		
		boolean success = sessionManager.restoreSession(conn, username, token);
		if (success) {
			sendWelcomeMessages(conn, token);
		}
    }

	private void handleMarkFeedback(JsonParser parser, WebSocket conn) {
        try {
            // get filename, exercise_type, mark
            String filename = parser.getString("filename");
            String exerciseType = parser.getString("exercise_type");
            String markString = parser.getString("mark");
            String token = parser.getString("id");
            Mark mark = null;
            try {
                mark = Mark.fromString(markString);
            } catch (Exception e) {
                ConnUtils.sendAlert(conn, "Mark Feedback Loop: Unrecognized desired mark: " + markString);
                return;
            }
            
            SourceDocument source = amendMark(filename, token, mark);
            
            updaters.updateMark(source, exerciseType, mark);
        } catch (Exception e) {
            ConnUtils.sendAlert(conn, e.getMessage());
            e.printStackTrace();
        }
    }

    private SourceDocument amendMark(String filename, String token, Mark mark) {
        // Finds the SourceDocument file of a student
        
        UserState userSession = sessionManager.getUserState(token);
        String title = userSession.getTitle();
        String studentNumber = userSession.getStudent();
        
        List<StudentJob> group = allJobs.getJobGroupByTitle(title);
        StudentJob studentJob = group.get(Integer.parseInt(studentNumber));
        studentJob.changeMark(mark);
        return studentJob.getSourceDocument(filename);
    }

    private void handleFeedback(JsonParser parser, WebSocket conn) {
        try {
            String annType = parser.getString("ann_type");
            String annotation = parser.getString("annotation");
            String fileName = parser.getString("filename");
            int lineno = parser.getInt("lineno");
            String token = parser.getString("id");
            
            SourceDocument doc = amendFile(fileName, lineno, annType, annotation, token);
            
            updaters.update(doc, lineno, annType, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SourceDocument amendFile(String fileName, int lineno, String annType, String annotation, String token) {
        // get the postpro.json file
        UserState userSession = sessionManager.getUserState(token);
        String title = userSession.getTitle();
        String studentNumber = userSession.getStudent();
        
        List<StudentJob> group = allJobs.getJobGroupByTitle(title);
        StudentJob studentJob = group.get(Integer.parseInt(studentNumber));
        return studentJob.amend(fileName, lineno, annType, annotation);
    }

    private void handleRetrieveResult(JsonParser parser, WebSocket conn) {

        String subtype = parser.getString("subtype");
        if (subtype == null || subtype.isEmpty()) {
            ConnUtils.sendAlert(conn, "retrieve_results: subtype can't be empty");
            return;
        }

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
        
        UserState userState = sessionManager.getUserState(parser.getString("id"));
        // update user state
        userState.setTitle(title);
        userState.setStudent(student);

        StudentJob studentJob = group.get(studentNumber);

        // Read the JSON file
        String fileContent = studentJob.readPostProcessorOutput();
        // Type of exercise
        String exerciseType = studentJob.getExerciseType();
        // Mark received
        String mark = studentJob.getMark();

        // Holds annotation information for each student-submitted file
        Map<String, List<AnnotationWrapper>> submissionFeedbackMap = generateSubmissionFeedbackMap(fileContent);

        // Create a set of file names from the annotations in the JSON output
        Set<String> uniqueFiles = new HashSet<>();
        JsonParser jsonParser = new JsonParser(fileContent);
        JsonArrayParser annotations = jsonParser.getArrayParser("annotations");
        for (JsonParser a : annotations) {
            String filePath = a.getString("filename");
            uniqueFiles.add(filePath);
        }

        switch (subtype) {
          case "postprocessor":
            sendPostprocessorMsg(title, student, fileContent, conn, exerciseType);
            break;
          case "annotated":
            sendAnnotatedMsg(uniqueFiles, submissionFeedbackMap, conn, exerciseType, mark);
            break;
          default:
            System.out.println("Unrecognized result message subtype");
        }
        
    }

    private void sendPostprocessorMsg(String title, String student, String fileContent, WebSocket conn, String exerciseType) {
        JSONObject postProcResultMsg = new JSONObject();
        postProcResultMsg.put("type", "postpro_result");
        postProcResultMsg.put("title", title);
        postProcResultMsg.put("student", student);
        postProcResultMsg.put("data", fileContent);
        postProcResultMsg.put("exercise_type", exerciseType);
        conn.send(postProcResultMsg.toJSONString());
    }

    private void sendAnnotatedMsg(Set<String> uniqueFiles, Map<String, List<AnnotationWrapper>> submissionFeedbackMap, WebSocket conn, String exerciseType, String mark) {
        // Generate a JSON message with an array containing JSON objects - each is made of the file name,
        // its contents and if a line has an annotation, its corresponding error.
        JSONObject annotatedFilesMsg = new JSONObject();
        annotatedFilesMsg.put("type", "annotated_files");
        JSONArray files = generateAnnotatedFileArray(uniqueFiles, submissionFeedbackMap);
        annotatedFilesMsg.put("files", files);
        annotatedFilesMsg.put("exercise_type", exerciseType);
        annotatedFilesMsg.put("mark", mark);
        conn.send(annotatedFilesMsg.toJSONString());
    }

    /**
     * Parses the postProcessor output, extracts info from annotations,
     * puts info in AnnotationWrapper and finally creates a mapping
     * between filename and list of associated annotations.
     * @param postProcessorOutput
     * @return map (filename, list of AnnotationWrappers)
     */
    private Map<String,List<AnnotationWrapper>> generateSubmissionFeedbackMap(String postProcessorOutput) {

        Map<String,List<AnnotationWrapper>> submissionFeedbackMap = new HashMap<>();

        JsonParser postprocessorParser = new JsonParser(postProcessorOutput);
        JSONArray annotations = postprocessorParser.getArray("annotations");
        JsonArrayParser annotationsParser = new JsonArrayParser(annotations);

        for (JsonParser jp : annotationsParser) {
            addToSubmissionFeedbackMap(submissionFeedbackMap, jp);
        }
        return submissionFeedbackMap;
    }

    private void addToSubmissionFeedbackMap(Map<String, List<AnnotationWrapper>> map, JsonParser annotation) {

        String fileName = annotation.getString("filename");
        int lineNumber = annotation.getInt("lineno");
        String text = annotation.getString("text");

        List<AnnotationWrapper> value = map.containsKey(fileName) ? map.get(fileName) : new ArrayList<>();
        value.add(new AnnotationWrapper(lineNumber, text));
        map.put(fileName, value);
    }

    /**
     * This inner class acts as a wrapper for the information
     * held in each object within the "annotations" JSON array of
     * the post processor JSON output.
     * It makes that information easier to access, so
     * removing the need to parse JSON every time.
     */
    private class AnnotationWrapper {

        private int lineNumber;
        private String text;

        AnnotationWrapper(int lineNumber, String text) {
            this.lineNumber = lineNumber;
            this.text = text;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getText() {
            return text;
        }
    }

    private JSONArray generateAnnotatedFileArray(Set<String> uniqueFiles, Map<String, List<AnnotationWrapper>> feedbackMap) {
        JSONArray filesWithAnnotations = new JSONArray();
        for (String uniqueFile : uniqueFiles) {
            Path filePath = Paths.get(uniqueFile);
            List<String> fileLines = FileUtils.splitFileOnNewLine(filePath);
            String fileName = FileUtils.getFileName(filePath);
            JSONObject annotatedFile = new JSONObject();
            annotatedFile.put("filename", fileName);
            JSONArray fileData = generateAnnotatedFile(filePath.toString(), feedbackMap, fileLines);
            annotatedFile.put("data", fileData);
            filesWithAnnotations.add(annotatedFile);
        }
        return filesWithAnnotations;
    }

    /**
     * Builds the fileData JSONArray, as follows:
     * <pre>
     * "data": [
     *     {"no": 3, "content": "import Data.Maybe", "annotation": ""}
     * ]
     * </pre>
     * @param filePath
     * @param feedbackMap
     * @param fileLines
     * @return fileData
     */
    private JSONArray generateAnnotatedFile(String filePath, Map<String, List<AnnotationWrapper>> feedbackMap, List<String> fileLines) {
        List<AnnotationWrapper> annotations = feedbackMap.get(filePath);

        // Ensure every line has a corresponding annotation string
        addEmptyAnnotationsForGoodLines(annotations, fileLines.size());

        Comparator<AnnotationWrapper> byLineNumber = (a1, a2) -> Integer.compare(
                a1.getLineNumber(), a2.getLineNumber());

        annotations = annotations.stream().sorted(byLineNumber).collect(Collectors.toList());

        JSONArray fileData = new JSONArray();
        
        for (AnnotationWrapper annotation : annotations) {
            JSONObject jsonObject = new JSONObject();
            int lineNumber = annotation.getLineNumber();
            jsonObject.put("no", lineNumber);
            jsonObject.put("content", fileLines.get(lineNumber - 1));
            jsonObject.put("annotation", annotation.getText());
            fileData.add(jsonObject);
        }
        return fileData;
    }

    /**
     * Complete the list of annotations to process into the JSON response (see generateAnnotatedFile)
     * with empty annotations for lines that did not receive any feedback.
     * @param annotations
     * @param numbOfFileLines
     */
    private void addEmptyAnnotationsForGoodLines(List<AnnotationWrapper> annotations, int numbOfFileLines) {
        // Get index referenced in feedback annotations
        Set<Integer> indicesWithFeedback;
        indicesWithFeedback = annotations.stream().map(AnnotationWrapper::getLineNumber).collect(Collectors.toSet());

        // Add empty annotation for lines without feedback
        for (int i = 1; i <= numbOfFileLines; i++) {
            if (!indicesWithFeedback.contains(i)) {
                annotations.add(new AnnotationWrapper(i, ""));
            }
        }
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
    	if (uncheckedCredentialsMessages.contains(parser.getString("type"))) {
    		return true;
    	}

        // Get the token
        String token = parser.getString("id");
        if (token == null) {
            return false;
        }

        // Get the corresponding connection
        UserSession expected = sessionManager.getSession(token);

        return conn == expected.getWebSocket();
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
                JSONObject gitobj = (JSONObject) l;
                String str = (String) gitobj.get("git");
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
        UserAccount userAccount = authenticator.authenticate(username, password);
        success = userAccount != null;

        // if login successful
        if (success) {
            String token = userAccount.generateToken(tokenGenerator);
            
            // Register in the session manager
            sessionManager.createSession(token, conn, userAccount);
        	
            sendWelcomeMessages(conn, token);

            return;
        }

        // if login fails
        else {
            sendLoginFailure(conn);
            return;
        }
    }

	private void sendLoginFailure(WebSocket conn) {
		JSONObject failure_msg = new JSONObject();
		failure_msg.put("type", "login_fail");
		conn.send(failure_msg.toJSONString());
	}

	private void sendWelcomeMessages(WebSocket conn, String token) {
		// send login success
		JSONObject success_msg = new JSONObject();
		success_msg.put("type", "login_success");
		success_msg.put("id", "" + token);
		conn.send(success_msg.toJSONString());

		// Send the currently existing jobs
		conn.send(getJobsListMessage().toJSONString());
		
		// Send the valid exercise types
		sendExerciseTypes(conn);
	}

    void sendExerciseTypes(WebSocket conn) {
        // get list of exercises from database
        String sql = "SELECT type FROM `ExerciseTypes`";
        List<Map<String, String>> results = db.retrieveQueryString(sql);
        if (results == null) {
            return;
        }
        List<String> validTypes = new ArrayList<>();
        for (Map<String, String> row : results) {
            String aType = row.get("type");
            validTypes.add(aType);
        }
        java.util.Collections.sort(validTypes);
        
        // create message object
        JSONObject msgobj = new JSONObject();

        msgobj.put("type", "typelist");
        JSONArray dataArray = new JSONArray();
        dataArray.addAll(validTypes);
        msgobj.put("data", dataArray);
        conn.send(msgobj.toJSONString());
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
