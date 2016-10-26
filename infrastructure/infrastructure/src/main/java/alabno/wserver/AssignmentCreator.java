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
		
		String clonerScriptPath = "infrastructure/cloner.py";
		StringBuilder studentGitArguments = new StringBuilder();
		
		for (int i = 0; i < studentGitLinks.size(); i++) {
			studentGitArguments.append(studentGitLinks.get(i) + " ");
		}
		
		List<String> command = Arrays.asList(
				"python",
				clonerScriptPath,
				"--extype",
				exerciseType,
				"--students",
				studentGitArguments.toString()
				);
		
		if (modelAnswerGitLink != null)
		{
			command.add("--model");
			command.add(modelAnswerGitLink);
		}
		
		ProcessBuilder pb = new ProcessBuilder(command);
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
		try {
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
