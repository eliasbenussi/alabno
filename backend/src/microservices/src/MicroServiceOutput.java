import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the output of a microservice.
 * The value of its fields can be easily transposed into
 * a JSON object.
 */
public class MicroServiceOutput {

    /* The total score for the submission */
    private int score;

    /* The list of annotations i.e. the feedback */
    private List<Annotation> annotations = new ArrayList<>();

    /* The first occurring error, if any */
    private String error = null;

    public MicroServiceOutput(int score) {
        this.score = score;
    }

    public MicroServiceOutput(int score, String error) {
        this.score = score;
        this.error = error;
    }

    public void addAnnotation(int lineNumber, int charNumber, String text) {
        annotations.add(new Annotation(lineNumber, charNumber, text));
    }

    /**
     * Render microService output as JSON object
     * @return
     */
    public JSONObject getJson() {

        JSONObject json = new JSONObject();
        json.put("Score", score);

        JSONArray jsonAnnotations = new JSONArray();

        // Create JSON object for each annotation and
        // add to JSON list
        for (Annotation annotation : annotations) {
            JSONObject jsonAnnotation = new JSONObject();
            jsonAnnotation.put("LineNo", annotation.getLineNumber());
            jsonAnnotation.put("CharNo", annotation.getCharNumber());
            jsonAnnotation.put("Text", annotation.getText());
            jsonAnnotations.add(jsonAnnotation);
        }

        // Add list to main JSON object
        json.put("Annotations", jsonAnnotations);
        json.put("Error", error);

        return json;
    }

    /* ----------------------------------------------------------- */

    private class Annotation {

        /**
         * This inner class wraps the elements
         * of a single feedback annotation within
         * the microService output
         */

        private int lineNumber;

        private int charNumber;

        private String text;

        public Annotation(int lineNumber, int charNumber, String text) {
            this.lineNumber = lineNumber;
            this.charNumber = charNumber;
            this.text = text;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getCharNumber() {
            return charNumber;
        }

        public String getText() {
            return text;
        }

    }

}
