
/* Encapsulates the configuration parameters for a MicroService */
public class MicroServiceConfiguration {

    private String inputDirectory;

    private String type;

    private String additionalConfigLocation;

    public MicroServiceConfiguration(String inputDirectory, String type, String additionalConfigLocation) {
        this.inputDirectory = inputDirectory;
        this.type = type;
        this.additionalConfigLocation = additionalConfigLocation;
    }

    public String getInputDirectory() {
        return inputDirectory;
    }

    public String getType() {
        return type;
    }

    public String getAdditionalConfigLocation() {
        return additionalConfigLocation;
    }

}
