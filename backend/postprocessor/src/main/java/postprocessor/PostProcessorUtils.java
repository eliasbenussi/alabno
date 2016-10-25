package postprocessor;

import json_parser.MicroServiceOutput;
import json_parser.MicroServiceOutputParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds utility methods used in the module
 */
public class PostProcessorUtils {

    public static List<MicroServiceOutput> getMicroServiceOutputsFromPaths(List<String> paths) {

        List<MicroServiceOutput> microServiceOutputs = new ArrayList<>();

        for (String path : paths) {
            microServiceOutputs.add(MicroServiceOutputParser.parseFile(new File(path)));
        }
        return microServiceOutputs;
    }

}
