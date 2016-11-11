package alabno.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class SubprocessUtilsTest {

    @Test
    public void test() {
        String cmd = "ls -lha /var";
        int code = SubprocessUtils.call(cmd);
        assertEquals(0, code);
    }

}
