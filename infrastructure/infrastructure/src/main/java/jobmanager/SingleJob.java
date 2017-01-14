package jobmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.json.simple.JSONObject;

public class SingleJob {

	private String input_directory;
	private String output_directory;
	private String type;
	private String additional_config;
	private String model_directory;

	private MicroServiceInfo the_service;

	public SingleJob(String input_directory, String output_directory, String type, String additional_config,
			MicroServiceInfo the_service, String model_directory) {
		this.input_directory = input_directory;
		this.output_directory = output_directory;
		this.type = type;
		this.additional_config = additional_config;
		this.the_service = the_service;
		this.model_directory = model_directory;
	}

	private String makeJsonFile() {
		JSONObject obj = new JSONObject();
		obj.put("input_directory", input_directory);
		obj.put("type", type);
		obj.put("additional_config", additional_config);
		if (model_directory != null)
		{
			obj.put("model_answer", model_directory);
		}
		return obj.toJSONString();
	}

	public void execute() {
		System.out.println("JobManager executing: " + the_service.getName());
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
		System.out.println(command);

		ProcessBuilder pb = new ProcessBuilder(Arrays.asList("/bin/sh", "-c", command));
		pb.redirectErrorStream(true);

		Process process;
		try {
			process = pb.start();
		} catch (IOException e2) {
			e2.printStackTrace();
			return;
		}
		
		BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = null;
		int code = -1;
		try {
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			code = process.waitFor();
			if (code != 0)
			{
				throw new Exception("Subprocess returned code " + code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
