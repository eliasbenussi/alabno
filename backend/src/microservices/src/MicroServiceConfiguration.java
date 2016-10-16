
/* Encapsulates the configuration parameters for a MicroService */
public class MicroServiceConfiguration {

    private String inputDirectory;

    private String type;

    private String additionalConfigLocation;

    private String outputDirectory;

    public MicroServiceConfiguration(String inputDirectory, String type,
                                     String additionalConfigLocation, String outputDirectory) {
        this.inputDirectory = inputDirectory;
        this.type = type;
        this.additionalConfigLocation = additionalConfigLocation;
        this.outputDirectory = outputDirectory;
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

    public String getOutputDirectory() {
        return outputDirectory;
    }

}
