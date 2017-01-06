package alabno.localjobstatus;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import alabno.wserver.JobsCollection;

public class LocalJobStatusAll {

    // Map from username to LocalJobStatus
    private Map<String, LocalJobStatus> instructors = new HashMap<>();
    private JobsCollection allJobs = null;

    public void addJob(String username, WebSocket conn, String jobname) {
        if (!instructors.containsKey(username)) {
            LocalJobStatus instructorJob = new LocalJobStatus(username, conn, allJobs);
            instructors.put(username, instructorJob);
        }
        LocalJobStatus theInstructorJob = instructors.get(username);
        theInstructorJob.updateSocket(conn); // Make sure that connection is up-to-date
        
        theInstructorJob.addJob(jobname);
    }
    
    public void updateJob(String username, String jobname, JobState state) {
        if (!instructors.containsKey(username)) {
            System.out.println("Error! could not find professor " + username + " in the LocalJobStatusAll!");
            return;
        }
        LocalJobStatus theInstructorJobs = instructors.get(username);
        theInstructorJobs.setState(jobname, state);
    }

    public void setJobsCollection(JobsCollection allJobs) {
        this.allJobs = allJobs;
    }
    
    public void removeJob(String username, String jobname, WebSocket conn) {
        if (!instructors.containsKey(username)) {
            LocalJobStatus instructorJob = new LocalJobStatus(username, conn, allJobs);
            instructors.put(username, instructorJob);
        }
        LocalJobStatus theInstructorJob = instructors.get(username);
        theInstructorJob.updateSocket(conn); // Make sure that connection is up-to-date
        
        theInstructorJob.removeJob(jobname);
    }

}
