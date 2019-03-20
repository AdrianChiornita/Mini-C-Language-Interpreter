package IMPInterpreter;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import IMPInterpreter.AST.IMPASTException;
import IMPInterpreter.IMPLexer.IMPLexerException;
import IMPInterpreter.IMPParser.IMPParserException;

public class IMPInterpreter {
	
	public final static String input = "input";
	public final static String output = "output";
	public final static String ast = "tree";
    

	public static void main(String[] args) throws IOException, IMPParserException, IMPLexerException {
		IMPParser parser = new IMPParser(
				new IMPLexer(
						new FileReader(input)
						)
				);
		 AST tree = parser.parse();
		 
		 BufferedWriter treewriter = new BufferedWriter(new FileWriter(ast));
		 treewriter.write(tree.get_root().show(0));
		 treewriter.close();
		 
		 PrintWriter outwriter = new PrintWriter(new FileWriter(output));
		 
		 try {
			tree.get_root().interpret();
			tree.check_for_errors();
			tree.get_memory().forEach((key, value) -> outwriter.println( key + "=" + value));
		 } catch (IMPASTException e) {
			 outwriter.println(e.getMessage());
		 } finally {
			 outwriter.close();
		 } 
	}
}
