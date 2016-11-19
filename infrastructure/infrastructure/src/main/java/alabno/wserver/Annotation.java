package alabno.wserver;

import org.json.simple.JSONObject;

public class Annotation {

    private String errortype;
    private String filename;
    private int lineno;
    private int charno;
    private String text;

    public Annotation(String errortype, String filename, int lineno, int charno, String text) {
        this.errortype = errortype;
        this.filename = filename;
        this.lineno = lineno;
        this.charno = charno;
        this.text = text;
    }

    public Annotation(JsonParser ann) {
        this.errortype = ann.getString("errortype");
        this.filename = ann.getString("filename");
        this.lineno = ann.getInt("lineno");
        this.charno = ann.getInt("charno");
        this.text = ann.getString("text");
    }

    public JSONObject toJsonObject() {
        JSONObject newAnn = new JSONObject();
        newAnn.put("errortype", errortype);
        newAnn.put("filename", filename);
        newAnn.put("lineno", lineno);
        newAnn.put("charno", charno);
        newAnn.put("text", text);
        return newAnn;
    }

    public String getErrortype() {
        return errortype;
    }

    public String getFilename() {
        return filename;
    }

    public int getLineno() {
        return lineno;
    }

    public int getCharno() {
        return charno;
    }

    public String getText() {
        return text;
    }

    public void amendError(JSONObject amended, String annType, String annotation) {
        amended.put("errortype", annType);
        amended.put("text", annotation);
    }

}
