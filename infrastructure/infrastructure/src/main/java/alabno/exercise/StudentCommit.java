package alabno.exercise;

import java.io.File;
import java.io.PrintWriter;

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
    
    private StudentJob parentStudentJob;
    private String hash;
    private String jsonLocation = null;
    private String status;
    private DatabaseConnection db;
    
    public StudentCommit(String hash, StudentJob parentStudentJob, DatabaseConnection db, String status) {
        this.hash = hash;
        this.parentStudentJob = parentStudentJob;
        this.status = status;
        this.db = db;
        
        updateDatabase();
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
            String exname = parentStudentJob.getExerciseName();
            String userindex = parentStudentJob.getStudentId();
            
            StringBuilder sb = new StringBuilder();
            sb.append(FileUtils.getWorkDir());
            sb.append("/");
            sb.append("tmp");
            sb.append("/");
            sb.append(exname);
            sb.append("/");
            sb.append("student" + userindex);
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
        return parentStudentJob.getExerciseType();
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
        JsonParser parser = getPostproJson();
        parser.putString("letter_score", mark.toString());
        parser.putDouble("number_score", mark.toDouble());
        rewriteJson(parser);
    }
    
    public void setParent(StudentJob parentStudentJob) {
        this.parentStudentJob = parentStudentJob;
    }
    
    public void updateDatabase() {
        TransactionBuilder tb = new TransactionBuilder();
        
        String title = parentStudentJob.getExerciseName();
        String exerciseType = getExerciseType();
        String uname = parentStudentJob.getUsername();
        String userindex = parentStudentJob.getStudentId();
        
        String sql = "REPLACE INTO `exercise`(`exname`, `extype`) VALUES (?,?)";
        String[] params = {title, exerciseType};
        tb.add(sql, params);
        
        // insert entries in the bigtable
        sql = "REPLACE INTO `exercise_big_table`(`exname`, `uname`, `userindex`, `hash`, `status`) VALUES (?,?,?,?,?)";
        params = new String[] {title, uname, userindex, hash, status};
        tb.add(sql, params);
        
        db.executeTransaction(tb);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        result = prime * result + ((parentStudentJob == null) ? 0 : parentStudentJob.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StudentCommit other = (StudentCommit) obj;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.equals(other.hash))
            return false;
        if (parentStudentJob == null) {
            if (other.parentStudentJob != null)
                return false;
        } else if (!parentStudentJob.equals(other.parentStudentJob))
            return false;
        return true;
    }

    public void tryMergeWith(StudentCommit commit) {
        if (this.equals(commit)) {
            if (!this.status.equals(commit.status)) {
                if (this.status.equals("ok") || commit.status.equals("ok")) {
                    this.status = "ok";
                } else if (this.status.equals("pending") || commit.status.equals("pending")) {
                    this.status = "pending";
                }
            }
        }
    }
    
    

}
