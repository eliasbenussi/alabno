package `simple-java-marker`

import java_antlr.Java8BaseVisitor
import java_antlr.Java8Parser
import org.antlr.v4.runtime.tree.ParseTree

class GuavaVisitor : Java8BaseVisitor<String>() {

    override fun visitIfThenElseStatement(ctx: Java8Parser.IfThenElseStatementContext): String {
        println("ifte " + ctx.text)

        val n = ctx.children.map { visit(it) }
        // This needs some work
        return n.toString()
    }

    override fun visitBasicForStatement(ctx: Java8Parser.BasicForStatementContext?): String {
        println("forS " + ctx?.text)

        val n = ctx?.children?.map { visit(it) }
        // This needs some work
        return n.toString()
    }

    override fun visitIfThenStatement(ctx: Java8Parser.IfThenStatementContext): String {
        println("if " + ctx.text)

        val n = ctx.children.map { visit(it) }
        // This needs some work
        return n.toString()
    }

    override fun visitCatchClause(ctx: Java8Parser.CatchClauseContext?): String {
        println("catchT " + ctx?.text)

        val n = ctx?.children?.map { visit(it) }
        // This needs some work
        return n.toString()
    }

    override fun visitMethodDeclaration(ctx: Java8Parser.MethodDeclarationContext): String {
        println("meth " + ctx.text)

        val n = ctx.children.map { visit(it) }
        // This needs some work
        return n.toString()
    }
}