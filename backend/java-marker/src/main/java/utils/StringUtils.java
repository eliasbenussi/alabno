package utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {


    public static List<Integer> formatLine(String line) {

        List<Integer> formattedContent = new ArrayList<>();
        for (char c : line.toCharArray()) {
            formattedContent.add((int) c);
        }
        return formattedContent;
    }
}
