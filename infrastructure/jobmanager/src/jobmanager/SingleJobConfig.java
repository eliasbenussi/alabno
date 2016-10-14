package jobmanager;

import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONObject;

public class SingleJobConfig {

	private String input_directory;
	private String output_directory;
	private String type;
	private String additional_config;

	private MicroServiceInfo the_service;

	public SingleJobConfig(String input_directory, String output_directory, String type, String additional_config,
			MicroServiceInfo the_service) {
		this.input_directory = input_directory;
		this.output_directory = output_directory;
		this.type = type;
		this.additional_config = additional_config;
		this.the_service = the_service;
	}

	private String makeJsonFile() {
		JSONObject obj = new JSONObject();
		obj.put("input_directory", input_directory);
		obj.put("type", type);
		obj.put("additional_config", additional_config);
		return obj.toJSONString();
	}

	public void execute() {
		// get the json string
		String jsonString = makeJsonFile();
		System.out.println(jsonString);

		// write the string to a file
		// to output_directory/service.json
		String input_json_path = "";
		try {
			input_json_path = output_directory + '/' + the_service.getName() + ".json";
			File input_json_file = new File(input_json_path);
			FileWriter fw = new FileWriter(input_json_file, false);
			fw.write(jsonString);
			fw.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// output location would be
		// output_directory/service_output.json
		String output_json_path = output_directory + '/' + the_service.getName() + "_output.json";

		// Execute
		// service_location inputfile outputlocation
		String command = the_service.getLocation() + " " + input_json_path + " " + output_json_path;
		Runtime rt = Runtime.getRuntime();
		System.out.println(command);
		try {
			Process pr = rt.exec(command);
			pr.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
