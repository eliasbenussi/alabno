package alabno.wserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;

public class AssignmentCreator implements Runnable {

	private String exerciseType;
	private String modelAnswerGitLink;
	private JSONArray studentGitLinks;

	public AssignmentCreator(String exerciseType, String modelAnswer, JSONArray studentGitLinks) {
		this.exerciseType = exerciseType;
		this.modelAnswerGitLink = modelAnswer;
		this.studentGitLinks = studentGitLinks;
	}

	@Override
	public void run() {
		try {
			String clonerScriptPath = "infrastructure/cloner.py";
			StringBuilder studentGitArguments = new StringBuilder();

			for (Object o : studentGitLinks) {
				studentGitArguments.append(o + " ");
			}

			List<String> command = new ArrayList<>();
			command.addAll(Arrays.asList("python", clonerScriptPath, "--extype", exerciseType, "--students",
					studentGitArguments.toString()));

			if (modelAnswerGitLink != null) {
				command.add("--model");
				command.add(modelAnswerGitLink);
			}

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);

			Process process = pb.start();

			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			int code = process.waitFor();
			System.out.println("Return code was " + code + "\n");

		} catch (IOException e) {
			System.out.println("Subprocess encountered an error");
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
