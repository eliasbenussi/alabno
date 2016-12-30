package alabno.exercise;

import java.util.ArrayList;
import java.util.List;

import alabno.database.DatabaseConnection;

public class StudentJob {

    private Exercise parentExercise;
    private List<StudentCommit> commits = new ArrayList<>();
    private String studentId;
    private String username;
    private DatabaseConnection db;
    
    public StudentJob(String username, String studentId, Exercise parentExercise, DatabaseConnection db) {
        this.username = username;
        this.studentId = studentId;
        this.parentExercise = parentExercise;
        this.db = db;
    }
    
    public String getStudentId() {
        return studentId;
    }

    public String getExerciseName() {
        return parentExercise.getTitle();
    }

    public String getExerciseType() {
        return parentExercise.getType();
    }
    
    public void setParent(Exercise parentExercise) {
        this.parentExercise = parentExercise;
    }
    
    public void addStudentCommit(StudentCommit commit) {
        commit.setParent(this);
        commits.add(commit);
    }
    
    public String getUsername() {
        return username;
    }

    public void addAllFrom(StudentJob job) {
        for (StudentCommit commit : job.commits) {
            for (StudentCommit c : this.commits) {
                c.tryMergeWith(commit);
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((parentExercise == null) ? 0 : parentExercise.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        StudentJob other = (StudentJob) obj;
        if (parentExercise == null) {
            if (other.parentExercise != null)
                return false;
        } else if (!parentExercise.equals(other.parentExercise))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
    
    
}
