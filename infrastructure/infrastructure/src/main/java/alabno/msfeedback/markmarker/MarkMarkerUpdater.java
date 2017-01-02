package alabno.msfeedback.markmarker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alabno.database.DatabaseConnection;
import alabno.msfeedback.Mark;
import alabno.msfeedback.MicroServiceUpdater;
import alabno.msfeedback.Runner;
import alabno.utils.FileUtils;
import alabno.utils.SubprocessUtils;
import alabno.wserver.SourceDocument;
import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;

public class MarkMarkerUpdater implements MicroServiceUpdater {

    private final DatabaseConnection conn;
    private static final String pathToMMarker = "backend/mark_marker/";
    private static final String trainingPath = FileUtils.getWorkDir() + "/" + pathToMMarker + "training";

    public MarkMarkerUpdater(DatabaseConnection conn) {
        this.conn = conn;
    }

    @Override
    public void init() {
        System.out.println("MarkMarker:init()");

        // create the directory
        SubprocessUtils.call("rm -rf " + trainingPath);
        SubprocessUtils.call("mkdir " + trainingPath);

        // Start updater thread
        Thread updaterThread = new Thread(new Runner(this, "MarkMarker"));
        updaterThread.start();
    }

    @Override
    public void update(SourceDocument source, int lineNumber, String type, String annotation) {
        // Does nothing
    }

    @SuppressWarnings("unchecked")
    public void updateTraining() {
        System.out.println("MarkMarkerUpdater:updateTraining()");
        String sql = "SELECT * FROM `MarkMarkerTest`";

        List<Map<String, String>> tuples = conn.retrieveQueryString(sql);
        Map<String, List<String>> tDatas = new HashMap<>();

        for (Map<String, String> t : tuples) {
            String name = (t.get("exercise"));
            String tData = (t.get("training_data"));
            if (!tDatas.containsKey(name)) {
                tDatas.put(name, new ArrayList<>());
            }
            tDatas.get(name).add(tData);
        }

        for (Map.Entry entry : tDatas.entrySet()) {
            try {
                final File f = new File(genPath((String) entry.getKey(), ".train"));
                FileWriter fw = new FileWriter(f);
                for (String s : (List<String>) entry.getValue()) {
                    fw.write(s);
                }
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String k : tDatas.keySet()) {
            ColumnDataClassifier cdc = new ColumnDataClassifier(pathToMMarker+"hs_basic_training.prop");
            final Classifier<String, String> classifier = cdc.makeClassifier(cdc.readTrainingExamples(genPath(k, ".train")));
            try {
                ByteArrayOutputStream fos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(classifier);
                oos.close();
                fos.close();
                FileOutputStream f = new FileOutputStream(new File(genPath(k, ".bin")));
                f.write(fos.toByteArray());
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(String.format("Update of %s completed", k));
            }
        }
    }

    private String genPath(String p, String ext) {
        return trainingPath + "/" + p + ext;
    }

    @Override
    public void updateMark(SourceDocument source, String exerciseType, Mark mark) {
        StringBuilder sb = new StringBuilder();
        source.getAllLines()
                .stream()
                .map(s -> s.replaceAll("\t", "\\t"))
                .map(s -> s.replaceAll("\n", ""))
                .forEach(s -> sb.append(s).append("\\n"));

        String markAndText = String.format("%s\t%s", mark.toString(), sb.toString());
        final String query = "INSERT INTO MarkMarkerTest (exercise, training_data) VALUES (?, ?)";
        conn.executeStatement(query, new String[]{exerciseType, markAndText});

        updateTraining();
    }
}
