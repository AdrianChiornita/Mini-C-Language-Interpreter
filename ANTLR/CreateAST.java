package ANTLR;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateAST {
	
	public final static String input = "input";
	public final static String ast = "antlr-tree";

	public static void main(String[] args) throws IOException {
			IMPGrammarLexer lexer = null;
	        CommonTokenStream tokenStream = null;
	        IMPGrammarParser parser = null;

	        CharStream inputstream = CharStreams.fromFileName(input);

	        lexer = new IMPGrammarLexer(inputstream);
	        tokenStream = new CommonTokenStream(lexer);

	        parser = new IMPGrammarParser(tokenStream);
	        ParserRuleContext tree = parser.program();

	        IMPVisitor visitor = new IMPVisitor();
	        
	        BufferedWriter treewriter = new BufferedWriter(new FileWriter(ast));
	        treewriter.write(visitor.visit(tree));
	        treewriter.close();
	}
}

