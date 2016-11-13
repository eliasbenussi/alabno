package alabno.msfeedback;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of registered Microservice Feedback Updaters
 *
 */
public class FeedbackUpdaters {

    private List<MicroServiceUpdater> updaters = new ArrayList<>();
    
    public void register(MicroServiceUpdater updater) {
        updaters.add(updater);
        updater.init();
    }
    
    public void updateAll(String source, String type, String annotation) {
        for (MicroServiceUpdater updater : updaters) {
            updater.update(source, type, annotation);
        }
    }
    
}
