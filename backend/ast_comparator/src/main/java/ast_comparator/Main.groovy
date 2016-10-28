package ast_comparator

import ast_comparator.antlr.Java8Lexer
import ast_comparator.antlr.Java8Parser
import json_parser.MicroServiceInputParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
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

            def tokens = new CommonTokenStream(new Java8Lexer(stream))
            def fileParser = new Java8Parser(tokens)
            def visit = new ASTGenerator()
            visit.visit(fileParser.primary())
        }
    }
    
}
