package alabno.utils;

import alabno.testsuite.TestUtils;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {

    @Test
    public void returnsJustFileNameWhenNoParentFolders() {

        String testPath = "/home/gj/git/alabno/tmp/532527877287062f6a6c0339eaded032/studentY/commitX/sequence.hs";
        Path path = Paths.get(testPath);
        String actual = FileUtils.getFileName(path);
        assertEquals("sequence.hs", actual);

    }

    @Test
    public void returnsJustFileNameWithParentFolders() {

        String testPath = "/home/gj/git/alabno/tmp/532527877287062f6a6c0339eaded032/studentY/commitX/sub/path/to/sequence.hs";
        Path path = Paths.get(testPath);
        String actual = FileUtils.getFileName(path);
        assertEquals("sub/path/to/sequence.hs", actual);

    }
}
