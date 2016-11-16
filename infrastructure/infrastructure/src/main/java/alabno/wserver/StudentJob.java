package alabno.wserver;

import alabno.utils.FileUtils;

/**
 * Represents the script of a single student
 *
 */
public class StudentJob {

    private String jsonLocation;
    
    /**
     * @param jsonLocation path where the json
     * produced by the postprocessor is located
     */
    public StudentJob(String jsonLocation) {
        this.jsonLocation = jsonLocation;
    }
    
    /**
     * @return the contents of the final json
     * output of the student
     */
    public String readPostProcessorOutput() {
        return FileUtils.read_file(jsonLocation);
    }

    public String getJsonLocation() {
        return jsonLocation;
    }
}
