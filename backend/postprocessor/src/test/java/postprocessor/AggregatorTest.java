package postprocessor;

import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AggregatorTest {

    @Test
    public void producesCorrectlyArrangedJSONFileFromAggregationOfTwoOutputFiles() {

        List<String> paths = new ArrayList<>();
        paths.add("src/test/testFiles/msOutputFile1.json");
        paths.add("src/test/testFiles/msOutputFile2.json");

        Aggregator aggregator = new Aggregator(paths);
        String actualOutput = aggregator.aggregate().toString();

        String pathToExpectedOutput = "src/test/testFiles/aggregationOf1and2.json";
        JSONObject expectedOutputJson = PostProcessorUtils.obtainJSONFile(pathToExpectedOutput);
        String expectedOutput = expectedOutputJson.toJSONString();

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void skipsMicroServiceOutputIfAnyErrorIsOccurredInTheMicroservices() {

        List<String> paths = new ArrayList<>();
        paths.add("src/test/testFiles/msOutputFile3.json");

        Aggregator aggregator = new Aggregator(paths);
        String actualOutput = aggregator.aggregate().toString();

        String pathToExpectedOutput = "src/test/testFiles/emptyAggregation.json";
        JSONObject expectedOutputJson = PostProcessorUtils.obtainJSONFile(pathToExpectedOutput);
        String expectedOutput = expectedOutputJson.toJSONString();

        assertEquals(expectedOutput, actualOutput);
    }
}
