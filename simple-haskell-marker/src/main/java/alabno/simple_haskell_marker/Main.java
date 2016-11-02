package alabno.simple_haskell_marker;

public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments(args);

        // Start the machine learning algorithm
        HaskellClassifier haskellClassifier = new HaskellClassifier();

        // Start the category converter
        CategoryConverter categoryConverter = new CategoryConverter();

        // call the Marker
        HaskellMarker marker = new HaskellMarker(haskellClassifier, arguments, categoryConverter);
        
        marker.mark();
    }

}
