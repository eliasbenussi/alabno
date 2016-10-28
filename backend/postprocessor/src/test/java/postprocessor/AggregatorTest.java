package postprocessor;

import json_parser.Error;
import json_parser.MicroServiceOutput;
import json_parser.MicroServiceOutputParser;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AggregatorTest {

    @Test
    public void returnsAnnotationsFromAllMicroservices() {

        List<MicroServiceOutput> microServiceOutputs = new ArrayList<>();
        MicroServiceOutput fstMicroserviceOutput = MicroServiceOutputParser.parseFile(new File("src/test/testFiles/msOutputFile1.json"));
        MicroServiceOutput sndMicroserviceOutput = MicroServiceOutputParser.parseFile(new File("src/test/testFiles/msOutputFile2.json"));

        List<Error> fstMicroserviceAnnotations = fstMicroserviceOutput.getAnnotations();
        List<Error> sndMicroserviceAnnotations = sndMicroserviceOutput.getAnnotations();

        microServiceOutputs.add(fstMicroserviceOutput);
        microServiceOutputs.add(sndMicroserviceOutput);

        Aggregator aggregator = new Aggregator(microServiceOutputs);
        List<Error> aggregatorResult = aggregator.aggregate();

        assertEquals(aggregatorResult.size(), fstMicroserviceAnnotations.size() + sndMicroserviceAnnotations.size());
        assertTrue(aggregatorResult.containsAll(fstMicroserviceAnnotations));
        assertTrue(aggregatorResult.containsAll(sndMicroserviceAnnotations));
    }

    @Test
    public void aggregatingAMicroserviceOuputWithEmptyAnnotationReturnsEmptyList() {

        List<MicroServiceOutput> microServiceOutputs = new ArrayList<>();
        MicroServiceOutput microserviceOutput = MicroServiceOutputParser.parseFile(new File("src/test/testFiles/msOutputFile4.json"));

        microServiceOutputs.add(microserviceOutput);

        Aggregator aggregator = new Aggregator(microServiceOutputs);
        List<Error> aggregatorResult = aggregator.aggregate();

        assertEquals(0, aggregatorResult.size());
    }
}
