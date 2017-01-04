package javamarker;

import org.junit.Test;
import utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void formatsStringsIntoListOfAsciiValuesCorrectly() {

        List<Integer> actual = StringUtils.formatLine("abcdef");
        Integer[] expected = {97, 98, 99, 100, 101, 102};
        assertEquals(Arrays.asList(expected), actual);
    }

}