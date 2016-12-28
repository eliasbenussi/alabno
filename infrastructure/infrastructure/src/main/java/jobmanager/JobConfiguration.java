package jobmanager;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import alabno.utils.FileUtils;
import alabno.wserver.JsonParser;

// Parses and contains the information of the current program run
// Takes the input from stdin
public class JobConfiguration {

	private String input_directory = "";
	private String model_directory = "";
	private String type = "";
	private String additional_config = "";
	private String output_directory = "";

	private List<MicroServiceInfo> services = new ArrayList<>();

	public JobConfiguration(String file_url) {
		try {
			read_configuration(file_url);
		} catch (ClassCastException e) {
			System.out.println("An error occurred when trying to parse the input JSON");
			System.exit(1);
		}
	}

	public String getAdditional_config() {
		return additional_config;
	}

	public String getInput_directory() {
		return input_directory;
	}

	public String getOutput_directory() {
		return output_directory;
	}

	public List<MicroServiceInfo> getServices() {
		return services;
	}

	public String getType() {
		return type;
	}

	// Reads the stdin, to get the configuration and
	// input/output paths
	private void read_configuration(String file_url) {
		String stdin_input = FileUtils.read_file(file_url);

		JsonParser parser = new JsonParser(stdin_input);
		
		if (!parser.isOk())
		{
			System.out.println("JobManager: malformed input JSON");
		}
		
		input_directory = parser.getString("input_directory");
		model_directory = parser.getString("model_directory");
		type = parser.getString("type");
		additional_config = parser.getString("additional_config");
		output_directory = parser.getString("output_directory");
		
		JSONArray services_array = (JSONArray) parser.getArray("services");
		for (int i = 0; i < services_array.size(); i++) {
			JSONObject an_object = (JSONObject) services_array.get(i);

			String name = (String) an_object.get("name");
			String location = (String) an_object.get("location");
			MicroServiceInfo an_info = new MicroServiceInfo(name, location);
			services.add(an_info);
		}

	}

	/**
	 * Executes the microservices in order
	 */
	public void runAllJobs() {
		for (MicroServiceInfo service : services)
		{
			SingleJob a_job = new SingleJob(input_directory, output_directory, type, additional_config, service, model_directory);
			a_job.execute();
		}
	}

	@Override
	public String toString() {
		return "JobConfiguration [input_directory=" + input_directory + ", type=" + type + ", additional_config="
				+ additional_config + ", output_directory=" + output_directory + ", services=" + services + "]";
	}

}
