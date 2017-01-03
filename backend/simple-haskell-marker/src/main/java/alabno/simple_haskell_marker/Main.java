package alabno.simple_haskell_marker;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        Arguments arguments = new Arguments(args);

        // Start the machine learning algorithm
        ScriptClassifier haskellClassifier = new HaskellClassifier(arguments);

        // Start the category converter
        CategoryConverterInterface categoryConverter = new CategoryConverter(arguments.getCategoryDataPath());

        // call the Marker
        HaskellMarker marker = new HaskellMarker(haskellClassifier, arguments, categoryConverter);
        
        marker.mark();
        marker.writeOutput();
    }

}
