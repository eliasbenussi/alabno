package ast_comparator

import json_parser.MicroServiceInputParser
import org.antlr.v4.runtime.ANTLRInputStream
import scala.NotImplementedError

class Main {

    static void main(String... args) {

        if (args.length < 2) {
            throw new IllegalArgumentException("Not enough arguments")
        }
        def inputParser = MicroServiceInputParser.parseFile(new File(args[0]))
        def path = inputParser.path
        def language = inputParser.getLanguage()

        switch (language) {
            case "java":
                javaANTLRRunner(path)
                break
            default: throw new NotImplementedError("Not yet done")
        }

    }

    static def javaANTLRRunner(String path) {
        File file = new File(path)
        def files = file.listFiles().findAll { it.getName().endsWith("java") }
        for (File f : files) {
            ANTLRInputStream stream = new ANTLRInputStream(new FileInputStream(f));
        }
    }
    
}
