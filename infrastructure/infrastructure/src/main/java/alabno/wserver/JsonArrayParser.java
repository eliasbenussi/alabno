package alabno.wserver;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class JsonArrayParser implements Iterable<JsonParser> {


    private final JSONArray jarray;

    public JsonArrayParser(JSONArray jarray) {
        this.jarray = jarray;
    }


    @Override
    public Iterator<JsonParser> iterator() {
        return new Iterator<JsonParser>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return jarray != null && currentIndex < jarray.size() && jarray.get(currentIndex) != null;
            }

            @Override
            public JsonParser next() {
                Object theObject = jarray.get(currentIndex);
                if (!(theObject instanceof JSONObject)) {
                    currentIndex++;
                    return null;
                }

                JsonParser out = new JsonParser((JSONObject) theObject);

                currentIndex++;

                return out;
            }

        };
    }
}
