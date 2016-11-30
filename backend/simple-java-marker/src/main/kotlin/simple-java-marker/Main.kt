import `simple-java-marker`.GuavaVisitor
import java_antlr.Java8Lexer
import java_antlr.Java8Parser
import json_parser.MicroServiceInputParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.FileInputStream

fun main(args: Array<String>) {

    if (args.size < 2) {
        throw IllegalArgumentException("Not enough arguments")
    }
    val inputParser = MicroServiceInputParser.parseFile(File(args[0]))
    val language = inputParser.language
    when {
        language.contains("java") -> {
            javaANTLRRunner(inputParser.path)
        }
        else -> throw IllegalArgumentException("Not so hello world")
    }
}

fun javaANTLRRunner(path: String) {
    val file = File(path)
    val files = file.listFiles().filter { it.name.endsWith(".java") }
    files.forEach(::println)
    files
            .map { ANTLRInputStream(FileInputStream(it)) }
            .map { CommonTokenStream(Java8Lexer(it)) }
            .map(::Java8Parser)
            .forEach {
                val visit = GuavaVisitor()
                visit.visit(it.compilationUnit())
            }
}