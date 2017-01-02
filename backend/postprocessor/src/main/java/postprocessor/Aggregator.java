package postprocessor;


import json_parser.Error;
import json_parser.MicroServiceOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Aggregator {

    /**
     * Used to check the position of an error to combine them
     */
    private class Position {
        private final String filename;
        private final int line;

        Position(Error e) {
            this(e.getFile(), e.getLineNo());
        }

        private Position(String filename, int line) {
            this.line = line;
            this.filename = filename;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position that = (Position) o;

            return line == that.line && filename.equals(that.filename);
        }

        @Override
        public int hashCode() {
            int result = filename.hashCode();
            result = 31 * result + line;
            return result;
        }
    }


    private final List<MicroServiceOutput> microServiceOutputs;

    public Aggregator(List<MicroServiceOutput> microServiceOutputs) {
        this.microServiceOutputs = microServiceOutputs;
    }

    /**
     * Regroup annotations from all MicroServices' json output provided
     * by errorType inside new JSON file.
     *
     * @return JSONArray with final, ordered output
     */
    public List<Error> aggregate() {
        Map<Position, Error> mAnnotations = new HashMap<>();

        List<Error> annotations = new ArrayList<>();

        for (MicroServiceOutput microServiceOutput : microServiceOutputs) {

            if (!microServiceOutput.getErrors().isEmpty()) {
                System.out.println("\nA MicroService failed to produce a valid output.\n" +
                        "Details on the errors:\n");
                microServiceOutput.getErrors().forEach(System.out::println);
            }
            microServiceOutput.getAnnotations().forEach(a -> addAnnotation(mAnnotations, a));
        }

        annotations.addAll(mAnnotations.values());
        return annotations;
    }


    private void addAnnotation(Map<Position, Error> map, Error annotation) {
        Position p = new Position(annotation);
        Error e = map.get(p);
        if (e == null) {
            map.put(p, annotation);
            return;
        }

        map.put(p,
                new Error(
                        e.getMsg() + " and " + annotation.getMsg(),
                        annotation.getFile(),
                        annotation.getLineNo(),
                        annotation.getColNo(),
                        e.getType() + " & " + annotation.getType()));
    }
}
