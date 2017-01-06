package alabno.wserver;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

import alabno.database.DatabaseConnection;
import alabno.executionunit.ExecutionUnitCreate;
import alabno.executionunit.ExecutionUnitUpdate;
import alabno.exercise.StudentCommit;
import alabno.localjobstatus.JobState;
import alabno.localjobstatus.LocalJobStatusAll;
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
import alabno.utils.ConnUtils.Color;
import alabno.utils.FileUtils;

public class WebSocketHandler {

    private ActiveSessions sessionManager = new ActiveSessions();
    private ExecutorService executor;
    private JobsCollection allJobs;
    private FeedbackUpdaters updaters;
    private DatabaseConnection db;
    private Authenticator authenticator;
	private TokenGenerator tokenGenerator;
	
	private Set<String> uncheckedCredentialsMessages = new HashSet<>();
    private Permissions permissions;
    private LocalJobStatusAll localJobs;

    public WebSocketHandler(ExecutorService executor, FeedbackUpdaters updaters, DatabaseConnection db, 
            Authenticator authenticator, TokenGenerator tokenGenerator, Permissions permissions,
            LocalJobStatusAll localJobs) {
        this.executor = executor;
        this.updaters = updaters;
        this.db = db;
        this.authenticator = authenticator;
        this.tokenGenerator = tokenGenerator;
        this.permissions = permissions;
        this.localJobs = localJobs;
        
        this.allJobs = new JobsCollection(this, this.db);
        this.localJobs.setJobsCollection(this.allJobs);
        
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
        case "std_refresh_list":
            handleStdRefreshList(parser, conn);
            break;
        case "std_retrieve_result":
            handleStdRetrieveResult(parser, conn);
            break;
        case "retrieve_commits":
            handleRetrieveCommits(parser, conn);
            break;
        case "refresh_commit":
            handleRefreshCommit(parser, conn);
            break;
        case "prof_delete_exercise":
            handleProfDeleteExercise(parser, conn);
            break;
        default:
            System.out.println("Unrecognized client message type " + type);
        }
    }
    
    private void handleProfDeleteExercise(JsonParser parser, WebSocket conn) {
        try {
            String title = parser.getString("title");
            if (!checkStringNotEmpty(title, "title", conn)) {
                throw new RuntimeException("title is empty");
            }
            
            // Get username
            UserSession session = sessionManager.getSession(parser);
            if (session == null) {
                ConnUtils.sendStatusInfo(conn, "No user session error", Color.RED, 5);
                throw new RuntimeException("no user session???");
            }
            
            UserAccount account = session.getAccount();
            if (account == null) {
                ConnUtils.sendStatusInfo(conn, "Error, no account found", Color.RED, 5);
                throw new RuntimeException("account is null");
            }
            
            String username = account.getUsername();
            
            // Delete on the allJobs
            allJobs.deleteExercise(title);
            
            // Delete from localJobs and send update
            localJobs.removeJob(username, title, conn);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        catch (Exception e) {
            ConnUtils.sendStatusInfo(conn, e.getMessage(), Color.RED, 5);
            e.printStackTrace();
            return;
        }

    }

    private boolean checkStringNotEmpty(String value, String name, WebSocket conn) {
        if (value == null || value.isEmpty()) {
            ConnUtils.sendStatusInfo(conn, "Error, " + name + " is empty", Color.RED, 5);
            return false;
        }
        return true;
    }
    
    private boolean checkStringNotEmptyMultiple(String[] values, String[] names, WebSocket conn) {
        if (values == null) {
            throw new RuntimeException("values is null");
        }
        if (names == null) {
            throw new RuntimeException("names is null");
        }
        if (values.length != names.length) {
            throw new RuntimeException("values.length=" + values.length + " names.length=" + names.length);
        }
        
        for (int i = 0; i < values.length; i++) {
            if (!checkStringNotEmpty(values[i], names[i], conn)) {
                return false;
            }
        }
        
        return true;
    }

    private void handleRefreshCommit(JsonParser parser, WebSocket conn) {

        String title = parser.getString("title");
        String student = parser.getString("student");
        
        // safety checks
        if (!checkStringNotEmpty(title, "title", conn)) {
            return;
        }
        
        // check student index validity
       if (!checkStringNotEmpty(student, "student index", conn)) {
           return;
       }

        // check if username can be found for given title and index in db
        String sql = "SELECT DISTINCT `uname` FROM `exercise_big_table` WHERE `exname` = ? AND `userindex` = ?";
        String[] params = {title, student};
        List<Map<String, Object>> results = db.retrieveStatement(sql, params);
        if (results == null || results.isEmpty()) {
            ConnUtils.sendStatusInfo(conn, "Error: could not find linked student username in database for selected exercise", Color.RED, 5);
            return;
        }
        if (results.size() > 1) {
            ConnUtils.sendStatusInfo(conn, "Database constraint violation: found more than 1 username for specific exercise and user index", Color.RED, 5);
            return;
        }

        ExecutionUnitUpdate updateJob = new ExecutionUnitUpdate(title, student, allJobs, db, conn);
        executor.submit(updateJob);
        
        ConnUtils.sendStatusInfo(conn, "Job sent", Color.BLACK, 5);
    }

    @SuppressWarnings("unchecked")
    private void handleRetrieveCommits(JsonParser parser, WebSocket conn) {
        String title = parser.getString("title");
        String studentid = parser.getString("student");
        
        // safety checks
        if (!checkStringNotEmpty(title, "title", conn) || !checkStringNotEmpty(studentid, "student index", conn)) {
            return;
        }

        List<String> commithashes = allJobs.getCommitsOfStudent(title, studentid);

        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "commits");
        JSONArray dataarray = new JSONArray();
        dataarray.addAll(commithashes);
        msgobj.put("data", dataarray);
        conn.send(msgobj.toJSONString());
    }

    private void handleStdRetrieveResult(JsonParser parser, WebSocket conn) {
        String title = parser.getString("title");
        String studentNumber = parser.getString("student");
        String hash = parser.getString("hash");
        
        // Safety checks
        if (!checkStringNotEmptyMultiple(
                new String[] {title, studentNumber, hash},
                new String[] {"title", "student index", "commit hash"},
                conn)) {
            return;
        }
        
        StudentCommit studentCommit = allJobs.findJob(title, studentNumber, hash);
        if (studentCommit == null) {
            ConnUtils.sendStatusInfo(conn, "Data not found on disk", Color.RED, 5);
            return;
        }
        if (!studentCommit.dataExists()) {
            ConnUtils.sendStatusInfo(conn, "Data not found on disk", Color.RED, 5);
            return;
        }
        
        UserState userState = sessionManager.getUserState(parser.getString("id"));
        // update user state
        System.out.println("Setting state for user token " + parser.getString("id"));
        System.out.println("title: " + title);
        System.out.println("student: " + studentNumber);
        userState.setTitle(title);
        userState.setStudent(studentNumber);

        // Read the JSON file
        String fileContent = studentCommit.readPostProcessorOutput();
        // Type of exercise
        String exerciseType = studentCommit.getExerciseType();
        // Mark received
        String mark = studentCommit.getMark();

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

        sendAnnotatedMsg(uniqueFiles, submissionFeedbackMap, conn, exerciseType, mark);

    }

    @SuppressWarnings("unchecked")
    private void handleStdRefreshList(JsonParser parser, WebSocket conn) {
        JSONObject msgobj = new JSONObject();
        
        msgobj.put("type", "std_ex_list");
        
        JSONArray data = new JSONArray();
        // get exercises of this student
        UserSession session = sessionManager.getSession(parser);
        if (session == null) {
            ConnUtils.sendStatusInfo(conn, "Error, could not find user session", Color.RED, 5);
            return;
        }
        
        UserAccount account = session.getAccount();
        if (account == null) {
            ConnUtils.sendStatusInfo(conn, "Error, no account linked to session", Color.RED, 5);
            return;
        }
        
        List<StudentCommit> jobs = allJobs.getJobsOfStudent(account);
        if (jobs == null) {
            ConnUtils.sendStatusInfo(conn, "Error, no jobs found", Color.RED, 5);
        }
        
        System.out.println("handleStdRefreshList: found " + jobs.size() + " jobs");
        
        // group up all student commits by exercise
        Map<String, List<StudentCommit>> exerciseMap = new HashMap<>();
        for (StudentCommit j : jobs) {
            // Skip commits that have no local data
            if (!j.dataExists()) {
                continue;
            }
            
            String exerciseName = j.getExerciseName();
            if (!exerciseMap.containsKey(exerciseName)) {
                exerciseMap.put(exerciseName, new ArrayList<>());
            }
            exerciseMap.get(exerciseName).add(j);
        }
        // generate data objects
        for (String key : exerciseMap.keySet()) {
            JSONObject dataobj = new JSONObject();
            dataobj.put("title", key);
            // add each of the commits
            JSONArray commits = new JSONArray();
            for (StudentCommit c : exerciseMap.get(key)) {
                JSONObject acommit = new JSONObject();
                acommit.put("stdno", c.getUserId());
                acommit.put("hash", c.getHash());
                acommit.put("status", c.getStatus());
                commits.add(acommit);
            }
            dataobj.put("commits", commits);
            data.add(dataobj);
        }
        msgobj.put("data", data);
        
        conn.send(msgobj.toJSONString());

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
		    UserSession session = sessionManager.getSession(token);
		    UserAccount account = session.getAccount();
			sendWelcomeMessages(conn, token, account.getUserType());
		}
    }

	private void handleMarkFeedback(JsonParser parser, WebSocket conn) {
        try {
            // get filename, exercise_type, mark
            String filename = parser.getString("filename");
            String exerciseType = parser.getString("exercise_type");
            String markString = parser.getString("mark");
            
            // safety checks
            if (!checkStringNotEmptyMultiple(
                    new String[] {filename, exerciseType, markString},
                    new String[] {"filename", "exercise type", "mark"},
                    conn)) {
                return;
            }
            
            String token = parser.getString("id");
            Mark mark = null;
            try {
                mark = Mark.fromString(markString);
            } catch (Exception e) {
                ConnUtils.sendStatusInfo(conn, "Mark Feedback Loop: Unrecognized desired mark: " + markString, Color.RED, 5);
                return;
            }
            
            SourceDocument source = amendMark(filename, token, mark);
            if (source == null) {
                ConnUtils.sendStatusInfo(conn, "No source document found for this exercise on disk", Color.RED, 5);
                return;
            }
            
            updaters.updateMark(source, exerciseType, mark);
        } catch (Exception e) {
            ConnUtils.sendStatusInfo(conn, e.getMessage(), Color.RED, 5);
            e.printStackTrace();
        }
    }

    private SourceDocument amendMark(String filename, String token, Mark mark) {
        // Finds the SourceDocument file of a student
        
        System.out.println("amendMark user token is " + token);
        
        UserState userSession = sessionManager.getUserState(token);
        if (userSession == null) {
            throw new RuntimeException("userSession is null");
        }
        String title = userSession.getTitle();
        String studentNumber = userSession.getStudent();
        
        StudentCommit studentCommit = allJobs.findJobLatest(title, studentNumber);
        if (studentCommit == null) {
            throw new RuntimeException("studentCommit is null");
        }
        if (!studentCommit.dataExists()) {
            throw new RuntimeException("No disk data for this student commit?");
        }
        studentCommit.changeMark(mark);
        return studentCommit.getSourceDocument(filename);
    }

    private void handleFeedback(JsonParser parser, WebSocket conn) {
        try {
            String annType = parser.getString("ann_type");
            String annotation = parser.getString("annotation");
            String fileName = parser.getString("filename");
            int lineno;
            try {
                lineno = parser.getInt("lineno");
            } catch (Exception e) {
                e.printStackTrace();
                ConnUtils.sendStatusInfo(conn, "Invalid line number", Color.RED, 5);
                return;
            }
            
            // safety checks
            if (!checkStringNotEmptyMultiple(
                    new String[] {annType, annotation, fileName},
                    new String[] {"annotation type", "annotation text", "file name"}
                    , conn)) {
                return;
            }
            
            String token = parser.getString("id");
            
            SourceDocument doc = amendFile(fileName, lineno, annType, annotation, token);
            if (doc == null) {
                ConnUtils.sendStatusInfo(conn, "Source document not found, cannot amend the annotation", Color.RED, 5);
                return;
            }
            
            updaters.update(doc, lineno, annType, annotation);
        } catch (Exception e) {
            e.printStackTrace();
            ConnUtils.sendStatusInfo(conn, e.getMessage(), Color.RED, 5);
        }
    }

    private SourceDocument amendFile(String fileName, int lineno, String annType, String annotation, String token) {
        // get the postpro.json file
        UserState userSession = sessionManager.getUserState(token);
        if (userSession == null) {
            throw new RuntimeException("No user session found");
        }
        String title = userSession.getTitle();
        String studentNumber = userSession.getStudent();
        
        StudentCommit studentCommit = allJobs.findJobLatest(title, studentNumber);
        if (studentCommit == null) {
            throw new RuntimeException("No student exercise data found!");
        }
        return studentCommit.amend(fileName, lineno, annType, annotation);
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

        UserState userState = sessionManager.getUserState(parser.getString("id"));
        // update user state
        System.out.println("Setting state for user token " + parser.getString("id"));
        System.out.println("title: " + title);
        System.out.println("student: " + student);
        userState.setTitle(title);
        userState.setStudent(student);

        StudentCommit studentCommit = allJobs.findJobLatest(title, student);
        if (studentCommit == null || !studentCommit.dataExists()) {
            ConnUtils.sendStatusInfo(conn, "Student commit exercise files not found", Color.RED, 5);
            return;
        }

        // Read the JSON file
        String fileContent = studentCommit.readPostProcessorOutput();
        // Type of exercise
        String exerciseType = studentCommit.getExerciseType();
        // Mark received
        String mark = studentCommit.getMark();

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
        
        // Discover the downloadable files in the output directory
        List<String> downloadablePaths = studentCommit.getDownloadablePaths();

        switch (subtype) {
          case "postprocessor":
            sendPostprocessorMsg(title, student, fileContent, conn, exerciseType);
            break;
          case "annotated":
            sendAnnotatedMsg(uniqueFiles, submissionFeedbackMap, conn, exerciseType, mark, downloadablePaths);
            break;
          default:
            System.out.println("Unrecognized result message subtype");
        }
        
    }

    @SuppressWarnings("unchecked")
    private void sendPostprocessorMsg(String title, String student, String fileContent, WebSocket conn, String exerciseType) {
        JSONObject postProcResultMsg = new JSONObject();
        postProcResultMsg.put("type", "postpro_result");
        postProcResultMsg.put("title", title);
        postProcResultMsg.put("student", student);
        postProcResultMsg.put("data", fileContent);
        postProcResultMsg.put("exercise_type", exerciseType);
        conn.send(postProcResultMsg.toJSONString());
    }

    @SuppressWarnings("unchecked")
    private void sendAnnotatedMsg(Set<String> uniqueFiles, Map<String, List<AnnotationWrapper>> submissionFeedbackMap, WebSocket conn, String exerciseType, String mark, List<String> downloadablePaths) {
        // Generate a JSON message with an array containing JSON objects - each is made of the file name,
        // its contents and if a line has an annotation, its corresponding error.
        JSONObject annotatedFilesMsg = new JSONObject();
        annotatedFilesMsg.put("type", "annotated_files");
        JSONArray files = generateAnnotatedFileArray(uniqueFiles, submissionFeedbackMap);
        annotatedFilesMsg.put("files", files);
        annotatedFilesMsg.put("exercise_type", exerciseType);
        annotatedFilesMsg.put("mark", mark);
        
        JSONArray downloads = new JSONArray();
        for (String path : downloadablePaths) {
            JSONObject pathobj = new JSONObject();
            pathobj.put("path", path);
            pathobj.put("name", FileUtils.filenameOf(path));
            downloads.add(pathobj);
        }
        annotatedFilesMsg.put("downloads", downloads);
        
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

    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private void handleGetJob(JsonParser parser, WebSocket conn) {
        String title = parser.getString("title");
        if (title == null || title.isEmpty()) {
            ConnUtils.sendAlert(conn, "Received request for null title");
            return;
        }
        
        List<StudentCommit> commits = allJobs.getStudentsByTitle(title);
        if (commits == null) {
            ConnUtils.sendStatusInfo(conn, "Could not retrieve student jobs", Color.RED, 5);
            throw new RuntimeException("Could not retrieve student jobs for exercise " + title);
        }

        // generate job_group message
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_group");
        msgobj.put("title", title);
        JSONArray groupArray = new JSONArray();
        for (StudentCommit c : commits) {
            if (!c.dataExists()) {
                c.removeFromDB();
                continue;
            }
            JSONObject studentobj = new JSONObject();
            studentobj.put("idx", c.getUserId());
            studentobj.put("uname", c.getUsername());
            groupArray.add(studentobj);
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

    @SuppressWarnings("unchecked")
    private void handleNewAssignment(JsonParser parser, WebSocket conn) {
        try {
            String title = parser.getString("title");

            if (title == null || title.isEmpty()) {
                ConnUtils.sendStatusInfo(conn, "Title is required", Color.RED, 5);
                return;
            }

            UserSession session = sessionManager.getSession(parser);
            if (session == null) {
                ConnUtils.sendStatusInfo(conn, "Could not find user session", Color.RED, 5);
                throw new RuntimeException("session is null");
            }
            UserAccount account = session.getAccount();
            if (account == null) {
                ConnUtils.sendStatusInfo(conn, "Could not find user account", Color.RED, 5);
                throw new RuntimeException("account is null");
            }
            String username = account.getUsername();
            
            String extype = parser.getString("ex_type");
            if (!checkStringNotEmpty(extype, "exercise type", conn)) {
                throw new RuntimeException("Exercise type is empty or null" + extype);
            }
            
            String modelgit = parser.getString("model_git");
            if (!checkStringNotEmpty(modelgit, "model solution git repository", conn)) {
                throw new RuntimeException("Model Solution git is " + modelgit);
            }
            if (!modelgit.startsWith("https://")) {
                ConnUtils.sendStatusInfo(conn, "Invalid git URL: " + modelgit, Color.RED, 5);
                throw new RuntimeException("invalid model solution git link");
            }
            
            // Check student gits and usernames
            JsonArrayParser studentsGitsArray = parser.getArrayParser("students_git");
            if (studentsGitsArray == null) {
                ConnUtils.sendStatusInfo(conn, "Please provide student git links and student usernames", Color.RED, 5);
                throw new RuntimeException("no student gits array");
            }
            // Analyze each of the students links and usernames
            JSONArray cleanedArray = new JSONArray();
            for (JsonParser studentdata : studentsGitsArray) {
                if (studentdata == null) {
                    continue;
                }
                String git = studentdata.getString("git");
                String uname = studentdata.getString("uname");
                boolean gitnull = git == null || git.isEmpty();
                boolean unamenull = uname == null || uname.isEmpty();
                // If the entire entry is empty, skip it
                if (gitnull && unamenull) {
                    continue;
                }
                if (!git.startsWith("https://")) {
                    ConnUtils.sendStatusInfo(conn, "Invalid git https link: " + git, Color.RED, 5);
                    throw new RuntimeException("invalid git " + git);
                }
                if (uname == null || uname.isEmpty()) {
                    ConnUtils.sendStatusInfo(conn, "Username for link " + git + " is required", Color.RED, 5);
                    throw new RuntimeException("no username" );
                }
                cleanedArray.add(studentdata.getObject());
            }
            parser.putArray("students_git", cleanedArray);
            
            // Create the new pending job
            localJobs.addJob(username, conn, title);
            
            // Call primary handler
            boolean success = handleNewAssignmentInternal(parser, conn, username);
            if (!success) {
                localJobs.updateJob(username, title, JobState.ERROR);
            }
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            ConnUtils.sendAlert(conn, content);
            return;
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean handleNewAssignmentInternal(JsonParser parser, WebSocket conn, String username) {
        String title = parser.getString("title");
        String exerciseType = parser.getString("ex_type");
        String modelAnswerGitLink = parser.getString("model_git");
        JSONArray studentGitLinks = parser.getArray("students_git");

        if (title == null || title.isEmpty()) {
            ConnUtils.sendAlert(conn, "title is required");
            return false;
        }

        if (exerciseType == null || exerciseType.isEmpty()) {
            ConnUtils.sendAlert(conn, "exercise type is required");
            return false;
        }

        if (modelAnswerGitLink == null || modelAnswerGitLink.isEmpty()) {
            ConnUtils.sendAlert(conn, "model answer git repository required");
            return false;
        }

        if (studentGitLinks == null) {
            ConnUtils.sendAlert(conn, "students repo git links required");
            return false;
        } else {
            String msg = checkStudentGitLinks(studentGitLinks);
            if (msg != null) {
                ConnUtils.sendAlert(conn, msg);
                return false;
            }
        }

        System.out.println("all checks passed");

        ExecutionUnitCreate newAssignmentProcessor = new ExecutionUnitCreate(title, exerciseType, modelAnswerGitLink,
                studentGitLinks, conn, allJobs, db, username, localJobs);

        executor.submit(newAssignmentProcessor);

        // Send confirmation message
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_sent");
        conn.send(msgobj.toJSONString());
        
        return true;
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
                    System.out.println("git link check failed for " + str);
                    return errormsg;
                }
            }
        } catch (ClassCastException e) {
            System.out.println("git link check failed by class cast exception");
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
        	
            sendWelcomeMessages(conn, token, userAccount.getUserType());

            return;
        }

        // if login fails
        else {
            sendLoginFailure(conn);
            return;
        }
    }

	@SuppressWarnings("unchecked")
    private void sendLoginFailure(WebSocket conn) {
		JSONObject failure_msg = new JSONObject();
		failure_msg.put("type", "login_fail");
		conn.send(failure_msg.toJSONString());
	}

	@SuppressWarnings("unchecked")
    private void sendWelcomeMessages(WebSocket conn, String token, UserType userType) {
		// send login success
		JSONObject success_msg = new JSONObject();
		success_msg.put("type", "login_success");
		success_msg.put("id", "" + token);
		
		String usertypeString = "";
		switch (userType) {
        case ADMIN:
            usertypeString = "a";
            break;
        case PROFESSOR:
            usertypeString = "p";
            break;
        case STUDENT:
            usertypeString = "s";
            break;
        default:
            throw new RuntimeException("Usertype not recognized: " + userType);
		}
		success_msg.put("usertype", usertypeString);
		
		conn.send(success_msg.toJSONString());

		// Send the currently existing jobs
		conn.send(getJobsListMessage().toJSONString());
		
		// Send the valid exercise types
		sendExerciseTypes(conn);
	}

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private JSONObject getJobsListMessage() {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_list");
        JSONArray jobs = new JSONArray();
        for (String name : allJobs.getJobNames()) {
            JSONObject jobobj = new JSONObject();
            jobobj.put("title", name);
            String status = "ok";
            jobobj.put("status", status);
            jobobj.put("local", FileUtils.jobDirectoryExists(name));
            jobs.add(jobobj);
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
