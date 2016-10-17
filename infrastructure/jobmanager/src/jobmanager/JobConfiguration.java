package jobmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

// Parses and contains the information of the current program run
// Takes the input from stdin
public class JobConfiguration {

	private String input_directory = "";
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
		String stdin_input = read_file(file_url);

		// Parse the JSON
		Object parsed = JSONValue.parse(stdin_input);
		JSONObject jobject = (JSONObject) parsed;

		// Read the info from the JSON
		input_directory = (String) jobject.get("input_directory");
		type = (String) jobject.get("type");
		additional_config = (String) jobject.get("additional_config");
		output_directory = (String) jobject.get("output_directory");

		// Get the JSON array
		JSONArray services_array = (JSONArray) jobject.get("services");
		for (int i = 0; i < services_array.size(); i++) {
			JSONObject an_object = (JSONObject) services_array.get(i);

			String name = (String) an_object.get("name");
			String location = (String) an_object.get("location");
			MicroServiceInfo an_info = new MicroServiceInfo(name, location);
			services.add(an_info);
		}

	}

	private String read_file(String file_url) {
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


	public void runAllJobs() {
		for (MicroServiceInfo service : services)
		{
			SingleJobConfig a_job = new SingleJobConfig(input_directory, output_directory, type, additional_config, service);
			a_job.execute();
		}
	}

	@Override
	public String toString() {
		return "JobConfiguration [input_directory=" + input_directory + ", type=" + type + ", additional_config="
				+ additional_config + ", output_directory=" + output_directory + ", services=" + services + "]";
	}


}
