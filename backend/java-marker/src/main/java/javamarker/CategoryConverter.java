package javamarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class CategoryConverter implements CategoryConverterInterface {

    private final Map<String, String> errorMap = new HashMap<>();
    private final Map<String, String> descriptionMap = new HashMap<>();

    // Handle categoryName : categoryNumber relation
    private final List<String> categoryList = new LinkedList<>();

    /**
     * The category_converter_map.csv file contains 3 columns
     * categorykey     errortype      annotationtext
     * It will be read in to generate the maps
     */
    public CategoryConverter() {
        URL dataFile = this.getClass().getClassLoader().getResource("category_converter_map.csv");
        init(dataFile.getPath());
    }

    public CategoryConverter(String string) {
        init(string);
    }

    private void init(String url) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(url));
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
            categoryList.add(components[0]);
        }
        scanner.close();
    }

    @Override
    public String getErrorType(String ann) {
        String out = errorMap.get(ann);
        if (out == null) {
            System.out.println("CategoryConverter: could not find ErrorType for [" + ann + "]");
        }
        return out;
    }

    @Override
    public String getDescription(String ann) {
        String out = descriptionMap.get(ann);
        if (out == null) {
            System.out.println("CategoryConverter: could not find ErrorType for [" + ann + "]");
        }
        return out;
    }
}
