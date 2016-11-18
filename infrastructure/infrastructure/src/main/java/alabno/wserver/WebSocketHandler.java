package alabno.wserver;

import alabno.msfeedback.FeedbackUpdaters;
import alabno.utils.ConnUtils;
import alabno.utils.FileUtils;
import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

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
        try {
            String source = parser.getString("source");
            String annType = parser.getString("ann_type");
            String annotation = parser.getString("annotation");
            String fileName = parser.getString("filename");
            int lineno = parser.getInt("lineno");
            String token = parser.getString("id");
            
            amendFile(fileName, lineno, annType, annotation, token);
            
            updaters.updateAll(source, annType, annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void amendFile(String fileName, int lineno, String annType, String annotation, String token) {
        // get the postpro.json file
        UserState userSession = sessionManager.getUserState(token);
        String title = userSession.getTitle();
        String studentNumber = userSession.getStudent();
        
        List<StudentJob> group = allJobs.getJobGroupByTitle(title);
        StudentJob studentJob = group.get(Integer.parseInt(studentNumber));
        studentJob.amend(fileName, lineno, annType, annotation);
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
            sendPostprocessorMsg(title, student, fileContent, conn);
            break;
          case "annotated":
            sendAnnotatedMsg(uniqueFiles, submissionFeedbackMap, conn);
            break;
          default:
            System.out.println("Unrecognized result message subtype");
        }
        
    }

    private void sendPostprocessorMsg(String title, String student, String fileContent, WebSocket conn) {
        JSONObject postProcResultMsg = new JSONObject();
        postProcResultMsg.put("type", "postpro_result");
        postProcResultMsg.put("title", title);
        postProcResultMsg.put("student", student);
        postProcResultMsg.put("data", fileContent);
        conn.send(postProcResultMsg.toJSONString());
    }

    private void sendAnnotatedMsg(Set<String> uniqueFiles, Map<String, List<AnnotationWrapper>> submissionFeedbackMap, WebSocket conn) {
        // Generate a JSON message with an array containing JSON objects - each is made of the file name,
        // its contents and if a line has an annotation, its corresponding error.
        JSONObject annotatedFilesMsg = new JSONObject();
        annotatedFilesMsg.put("type", "annotated_files");
        JSONArray files = generateAnnotatedFileArray(uniqueFiles, submissionFeedbackMap);
        annotatedFilesMsg.put("files", files);
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
