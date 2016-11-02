package alabno.utils;

import java.io.BufferedReader;
import java.io.FileReader;

public class FileUtils {

    public static String read_file(String file_url) {
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
