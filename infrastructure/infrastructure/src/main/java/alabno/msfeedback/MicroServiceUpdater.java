package alabno.msfeedback;

import alabno.wserver.SourceDocument;

public interface MicroServiceUpdater {

    public void init();

    public void update(SourceDocument source, int lineNumber, String type, String annotation);

    public void updateMark(SourceDocument source, String exerciseType, Mark mark);
    
}
