package alabno.simple_haskell_marker;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class CategoryConverterTest {

    @Test
    public void converterTest() throws MalformedURLException {
        URL url = new URL("file:samples/converter_test.csv");
        
        CategoryConverterInterface categoryConverter = new CategoryConverter(url);
        
        assertEquals(categoryConverter.getDescription("comment"), "comment");
        
        assertEquals(categoryConverter.getDescription("ok"), "ok");
        
        assertEquals(categoryConverter.getErrorType("comment"), "unknown");
        
        assertEquals(categoryConverter.getErrorType("ok"), "unknown");
        
        assertEquals(categoryConverter.getDescription("rubbish"), null);
    }

}
