
import java.io.File;

public class AbstractMicroService {

    /* Configuration for the microservice */
    private MicroServiceConfiguration microServiceConfig;

    /* PRE: a valid absolute path is always passed */
    private File outputFile;

    public AbstractMicroService(MicroServiceConfiguration microServiceConfig, File outputFile) {
        this.microServiceConfig = microServiceConfig;
        this.outputFile = outputFile;
    }

}
