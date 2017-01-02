package alabno.localjobstatus;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.wserver.JobsCollection;

public class LocalJobStatus {

    private String username;
    private Map<String, JobState> jobs = new HashMap<>();
    private WebSocket conn;
    private JobsCollection globalJobs;

    public LocalJobStatus(String username, WebSocket conn, JobsCollection globalJobs) {
        this.username = username;
        this.conn = conn;
        this.globalJobs = globalJobs;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, JobState> getJobs() {
        return jobs;
    }
    
    public void addJob(String jobname) {
        jobs.put(jobname, JobState.WAITING);
        sendUpdate();
    }
    
    public void setState(String jobname, JobState newstate) {
        if (newstate == JobState.FINISHED) {
            jobs.remove(jobname);
        } else {
            jobs.put(jobname, newstate);
        }
        sendUpdate();
    }
    
    @SuppressWarnings("unchecked")
    private void sendUpdate() {
        JSONObject msgobj = new JSONObject();
        msgobj.put("type", "job_list");
        JSONArray thejobs = new JSONArray();
        for (String name : jobs.keySet()) {
            JobState state = jobs.get(name);
            String stateString = "ok";
            switch (state) {
            case ERROR:
                stateString = "error";
                break;
            case FINISHED:
                jobs.remove(name);
                continue;
            case PROCESSING:
                stateString = "processing";
                break;
            case WAITING:
                stateString = "pending";
                break;
            default:
                throw new RuntimeException("Unrecognized job state " + state);
            }
            JSONObject jobobj = new JSONObject();
            jobobj.put("title", name);
            jobobj.put("status", stateString);
            thejobs.add(jobobj);
        }
        for (String name : globalJobs.getJobNames()) {
            JSONObject jobobj = new JSONObject();
            jobobj.put("title", name);
            String status = "ok";
            jobobj.put("status", status);
            thejobs.add(jobobj);
        }
        msgobj.put("jobs", thejobs);
        conn.send(msgobj.toJSONString());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        LocalJobStatus other = (LocalJobStatus) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public void updateSocket(WebSocket conn) {
        this.conn = conn;
    }

}
