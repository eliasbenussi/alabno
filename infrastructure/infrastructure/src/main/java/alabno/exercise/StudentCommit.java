package alabno.exercise;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.database.DatabaseConnection;
import alabno.database.TransactionBuilder;
import alabno.msfeedback.Mark;
import alabno.utils.FileUtils;
import alabno.utils.SubprocessUtils;
import alabno.wserver.Annotation;
import alabno.wserver.JsonArrayParser;
import alabno.wserver.JsonParser;
import alabno.wserver.SourceDocument;

public class StudentCommit {
    
    private static final Set<String> downloadableExtensions = new HashSet<String>();
    
    static {
        downloadableExtensions.add("htm");
        downloadableExtensions.add("html");
        downloadableExtensions.add("pdf");
    }
    
    private String hash;
    private String jsonLocation = null;
    private String status;
    private DatabaseConnection db;
    private String exname;
    private String extype;
    private String username;
    private String userid;
    
    public StudentCommit(String exname, String extype, String username, String userid, String hash, String status, DatabaseConnection db) {
        this.exname = exname;
        this.extype = extype;
        this.username = username;
        this.userid = userid;
        this.hash = hash;
        this.status = status;
        this.db = db;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * @return the contents of the final json
     * output of the student
     */
    public String readPostProcessorOutput() {
        return FileUtils.read_file(getJsonLocation());
    }

    public String getJsonLocation() {
        if (jsonLocation == null) {
            
            StringBuilder sb = new StringBuilder();
            sb.append(FileUtils.getWorkDir());
            sb.append("/");
            sb.append("tmp");
            sb.append("/");
            sb.append(exname);
            sb.append("/");
            sb.append("student" + userid);
            sb.append("/");
            sb.append("commit" + hash + "_out");
            sb.append("/");
            sb.append("postpro.json");
    
            String postproPath = sb.toString();
            System.out.println("Generated a json location from DB: " + postproPath);
    
            jsonLocation = postproPath;
        }
        return jsonLocation;
    }
    
    private JsonParser getPostproJson() {
        String postproContent = readPostProcessorOutput();
        return new JsonParser(postproContent);
    }

    public SourceDocument amend(String fileName, int lineno, String annType, String annotation) {
        if (!dataExists()) {
            throw new RuntimeException("Disk data does not exist");
        }
        
        if ("ok".equals(annotation)) {
            annotation = "";
        }
        
        String desiredFile = toAbsolute(fileName);
        
        // Read the source file
        SourceDocument doc = new SourceDocument(fileName, desiredFile);

        JsonParser parser = getPostproJson();
        
        // get the annotations part
        JsonArrayParser annotations = parser.getArrayParser("annotations");

        for (JsonParser ann : annotations) {
            
            Annotation anAnnotation = new Annotation(ann);
            
            String aFileName = anAnnotation.getFilename();
            int aLineNo = anAnnotation.getLineno();
            
            if (desiredFile.equals(aFileName) && lineno == aLineNo) {
                anAnnotation.amendError(ann.getObject(), annType, annotation);
                rewriteJson(parser);
                return doc;
            }
        }
        
        // If loop reaches, nothing was found
        System.out.println("Could not find entry to be amended. Adding a new one...");
        Annotation newAnnotation = new Annotation(annType, desiredFile, lineno, 1, annotation);
        JSONObject newAnn = newAnnotation.toJsonObject();
        
        // append to the existing array
        JSONArray annotationsArray = parser.getArray("annotations");
        if (annotationsArray == null) {
            annotationsArray = new JSONArray();
        }
        annotationsArray.add(newAnn);
        parser.putArray("annotations", annotationsArray);
        
        rewriteJson(parser);
        return doc;
        
    }
    
    private synchronized void rewriteJson(JsonParser parser) {
        try {
            SubprocessUtils.call("rm " + getJsonLocation());
            File jsonFile = new File(getJsonLocation());
            PrintWriter writer = new PrintWriter(jsonFile);
            writer.print(parser.getObject().toJSONString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String toAbsolute(String relpath) {
        // utility to convert to absolute path based on the relative one
        // the 17 removes the final part of the path containing _out/postpro.json
        return getJsonLocation().substring(0, Math.max(0, getJsonLocation().length() - 17)) + "/" + relpath;
    }
    
    public String getExerciseType() {
        return extype;
    }

    public SourceDocument getSourceDocument(String fileName) {

        String desiredFile = toAbsolute(fileName);
        
        // Read the source file
        return new SourceDocument(fileName, desiredFile);
    }

    public String getMark() {
        JsonParser parser = getPostproJson();
        return parser.getString("letter_score");
    }

    public void changeMark(Mark mark) {
        if (!dataExists()) {
            throw new RuntimeException("No data found on disk!");
        }
        JsonParser parser = getPostproJson();
        parser.putString("letter_score", mark.toString());
        parser.putDouble("number_score", mark.toDouble());
        rewriteJson(parser);
    }

    public void updateDatabase() {
        TransactionBuilder tb = new TransactionBuilder();
        
        String title = exname;
        String exerciseType = getExerciseType();
        String uname = username;
        String userindex = userid;
        
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`) VALUES (?,?)";
        String[] params = {title, exerciseType};
        tb.add(sql, params);
        
        // insert entries in the bigtable
        sql = "REPLACE INTO `exercise_big_table`(`exname`, `uname`, `userindex`, `hash`, `status`) VALUES (?,?,?,?,?)";
        params = new String[] {title, uname, userindex, hash, status};
        tb.add(sql, params);
        
        db.executeTransaction(tb);
    }

    public String getExerciseName() {
        return exname;
    }

    public String getUserId() {
        return this.userid;
    }

    public String getHash() {
        return this.hash;
    }

    public String getUsername() {
        return username;
    }
    
    public boolean dataExists() {
        boolean result = FileUtils.isFile(getJsonLocation());
        if (!result) {
            System.out.println("Disk file " + getJsonLocation() + " does not exist");
        }
        return result;
    }

    public void removeFromDB() {
        String sql = "DELETE FROM `exercise_big_table` WHERE `exname` = ? AND `uname` = ? AND `hash` = ?";
        String[] params = {exname, username, hash};
        db.executeStatement(sql, params);
    }

    public List<String> getDownloadablePaths() {
        String outputDirectory = getOutputDirectory();
        System.out.println("Discovering downloadable files in " + outputDirectory);
        String outputDirectoryRelativeToAlabno = getOutputDirectoryRelativeToAlabnoTmp();
        
        Set<String> results = new HashSet<>();
        
        findDownloadableInDirectory(outputDirectory, outputDirectoryRelativeToAlabno, results);
        findDownloadableInDirectory(originalCommitDirectory(), originalCommitDirectoryRelative(), results);

        List<String> finalOut = new ArrayList<String>();
        finalOut.addAll(results);
        return finalOut;
    }

    private String originalCommitDirectoryRelative() {
        StringBuilder sb = new StringBuilder();
        sb.append(exname);
        sb.append("/");
        sb.append("student" + userid);
        sb.append("/");
        sb.append("commit" + hash);

        return sb.toString();
    }

    private String originalCommitDirectory() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.getWorkDir());
        sb.append("/");
        sb.append("tmp");
        sb.append("/");
        sb.append(exname);
        sb.append("/");
        sb.append("student" + userid);
        sb.append("/");
        sb.append("commit" + hash);

        return sb.toString();
    }

    private void findDownloadableInDirectory(String outputDirectory, String outputDirectoryRelativeToAlabno,
            Set<String> results) {
        // List directory content
        File outputDirectoryFile = new File(outputDirectory);
        String[] dirContent = outputDirectoryFile.list(); // get only file names, not full paths
        for (String aFile : dirContent) {
            System.out.print(aFile + "\t");
            // get extension
            String extension = "";
            int i = aFile.lastIndexOf('.');
            if (i > 0) {
                extension = aFile.substring(i+1);
            }
            System.out.println(extension);
            
            // check in the allowed extensions
            if (downloadableExtensions.contains(extension)) {
                results.add(outputDirectoryRelativeToAlabno + "/" + aFile);
            }
        }
        
        System.out.println("Results are");
        for (String r : results) {
            System.out.println(r);
        }
    }
    
    
    private String getOutputDirectoryRelativeToAlabnoTmp() {
        StringBuilder sb = new StringBuilder();
        sb.append(exname);
        sb.append("/");
        sb.append("student" + userid);
        sb.append("/");
        sb.append("commit" + hash + "_out");

        return sb.toString();
    }

    private String getOutputDirectory() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtils.getWorkDir());
        sb.append("/");
        sb.append("tmp");
        sb.append("/");
        sb.append(exname);
        sb.append("/");
        sb.append("student" + userid);
        sb.append("/");
        sb.append("commit" + hash + "_out");

        return sb.toString();
    }

}
