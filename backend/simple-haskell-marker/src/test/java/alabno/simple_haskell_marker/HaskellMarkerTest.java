package alabno.simple_haskell_marker;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;

import static org.easymock.EasyMock.*;

import org.junit.Test;

public class HaskellMarkerTest {

    ScriptClassifier scriptClassifier = mock(ScriptClassifier.class);
    CategoryConverterInterface categoryConverter = mock(CategoryConverterInterface.class);
    
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
        JSONObject outputObject = haskellMarker.getOutputObject();
        
        verify(scriptClassifier);
        verify(categoryConverter);
        
        assertEquals(outputObject.get("score"), 0);
    }

}
