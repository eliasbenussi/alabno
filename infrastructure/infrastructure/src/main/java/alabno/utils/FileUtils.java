package alabno.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {
    
    private static String workDir = ".";
    
    public static void initWorkDir() {
        workDir = System.getProperty("user.dir");
    }
    
    /**
     * @return the working directory alabno (also root of the git repository)
     */
    public static String getWorkDir() {
        return workDir;
    }

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

    public static void rename(String oldName, String newName) throws IOException {
        // File (or directory) with old name
        File file = new File(oldName);

        // File (or directory) with new name
        File file2 = new File(newName);

        if (file2.exists()) {
            throw new java.io.IOException("file exists");
        }

        // Rename file (or directory)
        boolean success = file.renameTo(file2);
    }

}
