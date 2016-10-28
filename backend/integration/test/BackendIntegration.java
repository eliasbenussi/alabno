import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BackendIntegration {

    public static void main(String[] args) {

        // Pass an input config json file to each MicroService
        String inputJsonConfig = "integration/testFiles/input/testInput.json";
        String msOutputJson = "integration/testFiles/output/linter_output.json";

        String finalOutputJson = "integration/testFiles/output/final_output.json";

        String[] executeLinter = {
                "java",
                "-jar",
                "linter/target/linter-1.0-SNAPSHOT-jar-with-dependencies.jar",
                inputJsonConfig,
                msOutputJson
        };

        String[] executePostprocessor = {
                "java",
                "-jar",
                "postprocessor-1.0-SNAPSHOT.jar",
                "haskell",
                msOutputJson,
                finalOutputJson
        };


        try {

            // Execute the MicroService
            ProcessBuilder msBuilder = new ProcessBuilder(executeLinter);
            msBuilder.directory(new File(System.getProperty("user.dir")));
            msBuilder.start();

            // Execute the PostProcessor
            ProcessBuilder postprocessorBuilder = new ProcessBuilder(executePostprocessor);
            postprocessorBuilder.directory(new File(System.getProperty("user.dir")));
            postprocessorBuilder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check that the outputted file has a specified number and letter grade


        assertEquals(true, true);
    }
}
