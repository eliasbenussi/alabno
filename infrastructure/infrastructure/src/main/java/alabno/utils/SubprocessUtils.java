package alabno.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SubprocessUtils {

    public static int call(String cmd) {
        try {
            List<String> completeCommands = new ArrayList<>();
            completeCommands.add("/bin/bash");
            completeCommands.add("-c");
            completeCommands.add(cmd);
            
            System.out.println(">> " + cmd);

            ProcessBuilder pb = new ProcessBuilder(completeCommands);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            int code = process.waitFor();
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

}
