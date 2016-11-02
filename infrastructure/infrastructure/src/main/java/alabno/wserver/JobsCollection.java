package alabno.wserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;

import alabno.utils.ConnUtils;

/**
 * Contains the known CompositeJobs
 * in the system
 */
public class JobsCollection {
    
    // TODO allow to have states with jobs (ok, in progress, error)
    // and to update these states
    
    private final Map<String, List<StudentJob>> allJobs = new HashMap<>();
    private WebSocketHandler webSocketHandler;
    
    public JobsCollection(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    
    /**
     * adds the job to the repository of all executed
     * marking jobs
     * 
     * @param title the name of the job to be added
     * @param newJobs list of student that did the exercise
     * @param conn client connection who requested it
     */
    public synchronized void addJob(String title, List<StudentJob> newJobs, WebSocket conn) {

        if (allJobs.containsKey(title)) {
            ConnUtils.sendAlert(conn, "A job with name " + title + " already exists. It will not be added");
            return;
        }
        
        allJobs.put(title, newJobs);
        
        webSocketHandler.broadcastJobList();
    }

    /**
     * @return the list of all job titles known
     */
    public List<String> getJobNames() {
        List<String> out = new ArrayList<>();
        out.addAll(allJobs.keySet());
        return out;
    }
    
    /**
     * @param title the title of the job to be found
     * @return the list of student jobs with corresponding title
     */
    public List<StudentJob> getJobGroupByTitle(String title) {
        return allJobs.get(title);
    }

}
