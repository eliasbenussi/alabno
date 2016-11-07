package alabno.simple_haskell_marker;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments(args);

        // Start the machine learning algorithm
        ScriptClassifier haskellClassifier = new HaskellClassifier();

        // Start the category converter
        CategoryConverterInterface categoryConverter = new CategoryConverter();

        // call the Marker
        HaskellMarker marker = new HaskellMarker(haskellClassifier, arguments, categoryConverter);
        
        marker.mark();
        marker.writeOutput();
    }

}
