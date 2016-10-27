package postprocessor;

import json_parser.MicroServiceOutput;
import json_parser.MicroServiceOutputParser;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScorerTest {

    @Test
    public void returnsCorrectLetterAndNumberGradeFromScore() {

        List<MicroServiceOutput> microServiceOutputs = new ArrayList<>();

        MicroServiceOutput microServiceOutput = MicroServiceOutputParser.parseFile(new File("src/test/testFiles/msOutputFile3.json"));
        microServiceOutputs.add(microServiceOutput);

        Scorer scorer = new Scorer(microServiceOutputs);

        Double numberGrade = scorer.getNumberGrade();
        assertEquals(new Double(52.7), numberGrade);

        String letterGrade = scorer.getLetterGrade();
        assertEquals("C", letterGrade);
    }

}
