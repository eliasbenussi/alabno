package alabno.exercise;

import java.util.HashMap;
import java.util.Map;

import alabno.database.DatabaseConnection;

/**
 * Represents an exercise job. The Exercise has a name and type, and contains
 * the exercises of various students
 *
 */
public class Exercise {

    // Maps from student username to student job
    private Map<String, StudentJob> studentsMap = new HashMap<>();
    private String title;
    private String type;
    private DatabaseConnection db;

    public Exercise(String title, String type, DatabaseConnection db) {
        this.title = title;
        this.type = type;
        this.db = db;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public void addStudentJob(StudentJob job) {
        if (!studentsMap.containsKey(job.getUsername())) {
            String studentId = job.getStudentId();
            job.setParent(this);
            studentsMap.put(studentId, job);
            return;
        } else {
            studentsMap.get(job.getUsername()).addAllFrom(job);
            return;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        Exercise other = (Exercise) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }
    
    
}
