package alabno.simple_haskell_marker;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CategoryConverter implements CategoryConverterInterface {

    private final Map<String, String> errorMap = new HashMap<>();
    private final Map<String, String> descriptionMap = new HashMap<>();
    
    /**
     * The category_converter_map.csv file contains 3 columns
     * categorykey     errortype      annotationtext
     * It will be read in to generate the maps
     */
    public CategoryConverter() {
        URL dataFile = this.getClass().getClassLoader().getResource("category_converter_map.csv");
        init(dataFile);
    }
    
    public CategoryConverter(URL url) {
        init(url);
    }
    
    private void init(URL url) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(url.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line == null || line.isEmpty()) {
                continue;
            }
            String[] components = line.split("\\t");
            if (components.length != 3) {
                System.out.println("CategoryConverter: Bad input: " + line);
                continue;
            }
            errorMap.put(components[0], components[1]);
            descriptionMap.put(components[0], components[2]);
        }
        scanner.close();
    }

    @Override
    public String getErrorType(String ann) {
        return errorMap.get(ann);
    }

    @Override
    public String getDescription(String ann) {
        return descriptionMap.get(ann);
    }

}
