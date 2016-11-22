package alabno.wserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SourceDocument {

    private String name;
    private String path;
    private List<String> source;
    
    public SourceDocument(String name, String path, List<String> source) {
        this.name = name;
        this.path = path;
        this.source = source;
    }
    
    public SourceDocument(String name, String path) {
        this.name = name;
        this.path = path;
        loadFromDisk(path);
    }

    private void loadFromDisk(String path) {
        File sourceFile = new File(path);
        source = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(sourceFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        
        while (scanner.hasNextLine()) {
            source.add(scanner.nextLine());
        }
        scanner.close();
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getLine(int lineNumber) {
        try {
            return source.get(lineNumber);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public List<String> getAllLines() {
        return source;
    }
    
}
