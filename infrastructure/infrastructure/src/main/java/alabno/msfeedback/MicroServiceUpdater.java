package alabno.msfeedback;

/**
 * Created by eb1314 on 08/11/16.
 */
public interface MicroServiceUpdater {

    public void init();

    public void update(String source, String type, String annotation);

}
