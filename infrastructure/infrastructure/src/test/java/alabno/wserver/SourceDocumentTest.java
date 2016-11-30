package alabno.wserver;

import static org.junit.Assert.*;

import org.junit.Test;

public class SourceDocumentTest {

    @Test
    public void getFullTextTest() {
        SourceDocument sourceDocument = new SourceDocument("testdoc", "sample/splitting_test.txt");
        assertTrue(sourceDocument.getFullText().contains("abra\ncadabra"));
    }

}
