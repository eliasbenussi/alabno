package alabno.msfeedback;

import alabno.wserver.SourceDocument;

public interface MicroServiceUpdater {

    void init();

    void update(SourceDocument source, int lineNumber, String type, String annotation);

    void updateMark(SourceDocument source, String exerciseType, Mark mark);

    default void updateTraining() {}

}
