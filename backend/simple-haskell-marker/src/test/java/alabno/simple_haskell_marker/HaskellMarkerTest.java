package alabno.simple_haskell_marker;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import json_parser.MicroServiceOutput;
import org.junit.Test;

public class HaskellMarkerTest {

    private ScriptClassifier scriptClassifier = mock(ScriptClassifier.class);
    private CategoryConverterInterface categoryConverter = mock(CategoryConverterInterface.class);
    
    @Test
    public void simpleCase() {
        // Expects the Classifier to be called once
        scriptClassifier.classify(anyObject());
        
        replay(scriptClassifier);
        replay(categoryConverter);
        
        Arguments arguments = new Arguments(new String[] {"samples/hs_basic_training.train", "test.json", "samples/marker_test.hs"});
        HaskellMarker haskellMarker = new HaskellMarker(scriptClassifier, arguments, categoryConverter);
        
        haskellMarker.mark();
        
        // get the output json object
        MicroServiceOutput outputObject = haskellMarker.getOutputObject();
        
        verify(scriptClassifier);
        verify(categoryConverter);
        
        assertEquals(outputObject.getScore(),0.0, 0);
    }

}
