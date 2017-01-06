package alabno.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtils {

    private static String workDir = ".";
    private static final Pattern pattern = Pattern.compile("commit.");

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
    
    public static boolean isFile(String fileUrl) {
        File f = new File(fileUrl);
        return f.exists() && !f.isDirectory();
    }

    /**
     * Split the file on new-line
     * and group the lines in a list.
     * N.B.: the lines are indexed from 0 in the list.
     * @param filePath
     * @return list of lines
     */
    public static List<String> splitFileOnNewLine(Path filePath) {

        List<String> linesList = new ArrayList<>();
        File file = new File(filePath.toString());
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                linesList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linesList;
    }

    /**
     * Extract file name from path with parent directories
     * (up to commitX/ exclusive.)
     * @param filePath
     * @return file name
     */
    public static String getFileName(Path filePath) {

        // Extract final name
        String fileName = filePath.getFileName().toString();

        // Work backwards to get everything up to .../commitX/
        Path parentPath = filePath.getParent();

        String parentDir = "";
        String out = "";
        while (!pattern.matcher(parentDir = parentPath.getFileName().toString()).find()) {
            out = parentDir + "/" + out;
            parentPath = parentPath.getParent();
        }

        out += fileName;
        return out;
    }

    public static boolean jobDirectoryExists(String jobname) {
        // Directory to check is alabno/tmp/jobname
        String checkDir = getWorkDir() + "/tmp/" + jobname;
        System.out.println("jobDirectoryExists checking " + checkDir);
        return Files.exists(Paths.get(checkDir));
    }

    /**
     * @param path
     * @return the final part of the path, namely only the name of the file
     */
    public static String filenameOf(String path) {
        String[] split = path.split("/");
        if (split == null) {
            throw new RuntimeException("Error, path split returned null");
        }
        if (split.length == 0) {
            return path;
        }
        if (split.length == 1) {
            return path;
        }
        return split[split.length - 1];
    }
}
