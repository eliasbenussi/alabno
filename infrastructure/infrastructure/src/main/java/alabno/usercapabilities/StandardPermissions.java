package alabno.usercapabilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import alabno.useraccount.UserType;
import alabno.utils.FileUtils;

public class StandardPermissions implements Permissions {

    private static final String capabilitiesFileName = "config/capabilities.tsv";

    private Set<String> studentMap = new HashSet<>();
    private Set<String> professorMap = new HashSet<>();
    private Set<String> adminMap = new HashSet<>();

    public StandardPermissions() {
        loadCapabilities();
    }

    private void loadCapabilities() {
        File capabilitiesFile = new File(FileUtils.getWorkDir() + "/" + capabilitiesFileName);
        Scanner scanner = null;
        try {
            scanner = new Scanner(capabilitiesFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            String[] words = line.split("\\t");
            if (words.length < 1) {
                continue;
            }
            String capabilityName = words[0];
            for (int i = 1; i < words.length; i++) {
                String userType = words[i];
                switch (userType) {
                case "a":
                    adminMap.add(capabilityName);
                    break;
                case "p":
                    professorMap.add(capabilityName);
                    break;
                case "s":
                    studentMap.add(capabilityName);
                    break;
                default:
                    break;
                }
            }
        }
        
        scanner.close();
    }

    @Override
    public boolean canPerform(UserType userType, String action) {
        switch (userType) {
        case ADMIN:
            return adminMap.contains(action);
        case PROFESSOR:
            return professorMap.contains(action);
        case STUDENT:
            return studentMap.contains(action);
        default:
            return false;
        }
    }

}
