package alabno.wserver;

public class UserState {

    // title and student are updated when receiving message retrieve_result
    private String title = null;
    private String student = null;

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

}
