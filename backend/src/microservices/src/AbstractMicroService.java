
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileReader;

public abstract class AbstractMicroService {

    /* Configuration for the microservice */
    private MicroServiceConfiguration microServiceConfig;

    /**
     * Read the configuration file and instantiate MicroService configuration
     * @param fileUrl
     */
    protected void readConfiguration(String fileUrl) {
        String input = readFile(fileUrl);

        try {
            // Parse the JSON
            Object parsed = JSONValue.parse(input);
            JSONObject jsonObject = (JSONObject) parsed;

            // Read the info from the JSON
            String inputDirectory = (String) jsonObject.get("input_directory");
            String type = (String) jsonObject.get("type");
            String additionalConfig = (String) jsonObject.get("additional_config");
            String outputDirectory = (String) jsonObject.get("output_directory");

            microServiceConfig = new MicroServiceConfiguration(inputDirectory, type, additionalConfig, outputDirectory);
        } catch (ClassCastException e) {
            System.out.println("An error occurred when trying to parse the input JSON");
            System.exit(1);
        }

    }

    public MicroServiceConfiguration getMicroServiceConfig() {
        return microServiceConfig;
    }
    
    private String readFile(String file_url) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file_url));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            br.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
