package alabno.msfeedback;

public interface MicroServiceUpdater {

    public void init();

    public void update(String source, String type, String annotation);

}
