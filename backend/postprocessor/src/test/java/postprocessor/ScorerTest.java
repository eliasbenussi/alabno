//package postprocessor;
//
//import org.junit.Test;
//import java.util.HashMap;
//import java.util.Map;
//import static org.junit.Assert.assertEquals;
//
//public class ScorerTest {
//
//    @Test
//    public void returnsAIfScoreIsBetween70And80() {
//        Map<String, Double> microServiceScores = new HashMap<>();
//        fillMicroServiceScoreMap(microServiceScores);
//        Scorer scorer = new Scorer(microServiceScores);
//        String actual = scorer.getLetterGrade();
//        String expected = "A";
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void getScoreReturnsNumberAndLetterScores() {
//        Map<String, Double> microServiceScores = new HashMap<>();
//        fillMicroServiceScoreMap(microServiceScores);
//        Scorer scorer = new Scorer(microServiceScores);
//        String actual = scorer.getScore().toJSONString();
//        String expected = "[{\"number\":77.85},{\"letter\":\"A\"}]";
//
//        assertEquals(expected, actual);
//    }
//
//    private void fillMicroServiceScoreMap(Map<String, Double> microServiceScores) {
//        String microService1 = "linter";
//        Double microService1Grade = 75.6;
//        String microService2 = "checker";
//        Double microService2Grade = 80.1;
//
//        microServiceScores.put(microService1, microService1Grade);
//        microServiceScores.put(microService2, microService2Grade);
//    }
//}
