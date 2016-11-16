package alabno.wserver;

import static org.junit.Assert.*;

import org.junit.Test;

public class StudentJobTest {

    @Test
    public void testPathConversion() {
        StudentJob job = new StudentJob("/home/gj/git/alabno/tmp/a21f4da16be95e94f4f9da6842ee7c7b/student0/commitX_out/postpro.json");
        
        assertEquals("/home/gj/git/alabno/tmp/a21f4da16be95e94f4f9da6842ee7c7b/student0/commitX/test.hs", job.toAbsolute("test.hs"));
    }

}
