import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class BackendIntegration {

    public static void main(String[] args) {

        // Pass an input config json file to each MicroService
        String inputJsonConfig = "integration/testFiles/input/testInput.json";
        String msOutputJson = "integration/testFiles/output/linterOutput.json";

        String finalOutputJson = "integration/testFiles/output/finalOutput.json";

        String[] executeLinter = {
                "java",
                "-jar",
                "linter/target/linter-1.0-SNAPSHOT-jar-with-dependencies.jar",
                inputJsonConfig,
                msOutputJson,
        };

        String[] executePostprocessor = {
                "java",
                "-jar",
                "postprocessor/target/postprocessor-1.0-SNAPSHOT-jar-with-dependencies.jar",
                "haskell",
                msOutputJson,
                finalOutputJson
        };


        try {

            // Execute the MicroService
            ProcessBuilder microServiceBuilder = new ProcessBuilder(executeLinter);
            Process msProcess = microServiceBuilder.start();
            msProcess.waitFor();


            // Execute the PostProcessor
            ProcessBuilder postprocessorBuilder = new ProcessBuilder(executePostprocessor);
            Process postprocessorProcess = postprocessorBuilder.start();
            postprocessorProcess.waitFor();


            // Check that the outputted file has a number and letter grade attributes
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(finalOutputJson));

            String letterGrade = (String) jsonObject.get("letter_score");
            Double numberGrade = (Double) jsonObject.get("number_score");

            assert(letterGrade != null);
            assert(numberGrade != null);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
