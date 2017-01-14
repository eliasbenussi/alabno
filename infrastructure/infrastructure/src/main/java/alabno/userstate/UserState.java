package alabno.userstate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserState {

    // title and student are updated when receiving message retrieve_result
    private String title = null;
    private String student = null;
    private Set<String> downloadablePaths = new HashSet<>();

    /**
     * @return the title of the document the user is currently viewing
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the name of the student document the user is currently viewing
     */
    public String getStudent() {
        return student;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public void addDownloadable(List<String> newpaths) {
        downloadablePaths.addAll(newpaths);
    }
    
    public boolean canDownload(String path) {
        return downloadablePaths.contains(path);
    }

}
