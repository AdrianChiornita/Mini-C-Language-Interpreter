package IMPInterpreter;

import java.io.IOException;

import IMPInterpreter.AST.*;
import IMPInterpreter.IMPLexer.IMPLexerException;

class IMPParser {
	private IMPLexer lexer;
	private IMPToken curent_token;
	private AST tree;
	
	@SuppressWarnings("serial")
	class IMPParserException extends Exception{
		public IMPParserException() {
			super("Syntax error " + curent_token.get_line_number());
		}
	}
	
	public IMPParser(IMPLexer lexer) throws IOException, IMPLexerException {
		this.lexer = lexer;
		this.curent_token = this.lexer.yylex();
		this.tree = new AST();
	}
	
	public IMPLexer get_lexer() {
		return lexer;
	}
	
	public void set_lexer(IMPLexer lexer) {
		this.lexer = lexer;
	}
	
	public IMPToken get_curent_token() {
		return curent_token;
	}
	
	public AST parse() throws IMPParserException, IOException, IMPLexerException {
		tree.set_root(program());
		
		if (this.curent_token.get_type() != IMPTokenType.EOF)
			throw new IMPParserException();
		return tree;
	}
	
	private MainProgram program() throws IMPParserException, IOException, IMPLexerException {
		var_declaration();
		
		Statement statement = statement();
		if (statement == null) 
			throw new IMPParserException(); 
		
		MainProgram node = tree.new MainProgram(statement);
		node.set_line_number(this.curent_token.get_line_number());
		
		return node;
	}

	private Statement statement() throws IMPParserException, IOException, IMPLexerException {
		Statement first;
		
		switch (this.curent_token.get_type()) {
			case START_BLOCK:
				first = block();
				break;
			case START_ASSIGN:
				first = assign();
				break;
			case IF:
				first = ifstatement();
				break;
			case WHILE:
				first = whilestatement();
				break;
			default:
				first = null;
				break;
		}
		
		if (first == null) return null;
		
		Statement second = statement();
		if (second != null) {
			first = tree.new SequenceStatement(first, second);
			first.set_line_number(this.curent_token.get_line_number());
		}
		
		return first;
	}

	private WhileStatement whilestatement() throws IOException, IMPParserException, IMPLexerException {
		eat(IMPTokenType.WHILE);
		
		Expression condition = expression();
		condition.set_line_number(this.curent_token.get_line_number());
		
		BlockStatement block = block();
		block.set_line_number(this.curent_token.get_line_number());
		
		return tree.new WhileStatement(condition, block);
	}

	private IfStatement ifstatement() throws IOException, IMPParserException, IMPLexerException {
		eat(IMPTokenType.IF);
		
		Expression condition = expression();
		condition.set_line_number(this.curent_token.get_line_number());
		
		BlockStatement _then = block();
		_then.set_line_number(this.curent_token.get_line_number());
		
		eat(IMPTokenType.ELSE);
		
		BlockStatement _else = block();
		_else.set_line_number(this.curent_token.get_line_number());
		
		return tree.new IfStatement(condition, _then, _else);
	}

	private AssignmentStatement assign() throws IOException, IMPParserException, IMPLexerException {
		String identifier = this.curent_token.get_value();
		identifier = identifier.substring(0, identifier.length() - 1).trim();
		
		eat(IMPTokenType.START_ASSIGN);
		
		AssignmentStatement ret =  tree.new AssignmentStatement(
					expression(),
					tree.new Variable(identifier)
				);
		ret.set_line_number(this.curent_token.get_line_number());
		
		eat(IMPTokenType.STOP_ASSIGN_OR_DECLARATION);
		return ret;
	}

	private BlockStatement block() throws IOException, IMPParserException, IMPLexerException {
		eat(IMPTokenType.START_BLOCK);
		
		Statement ret = statement();
		if (ret != null)
			ret.set_line_number(this.curent_token.get_line_number());
		
		eat(IMPTokenType.STOP_BLOCK);
		return tree.new BlockStatement(ret);	
	}
	
	private void var_declaration() throws IOException, IMPParserException, IMPLexerException {
		eat(IMPTokenType.START_DECLARATION);
		while (this.curent_token.get_type() != IMPTokenType.STOP_ASSIGN_OR_DECLARATION) {
			switch (this.curent_token.get_type()) {
				case START_ASSIGN:
					String identifier = this.curent_token.get_value();
					identifier = identifier.substring(0, identifier.length() - 1).trim();
					eat(IMPTokenType.START_ASSIGN);
					
					if (this.curent_token.get_type() != IMPTokenType.NUMBER)
						throw new IMPParserException(); 
					
					tree.get_memory().put(
							identifier,
							Integer.parseInt(this.curent_token.get_value())
							);
					eat(IMPTokenType.NUMBER);
					break;
				case VARIABLE:
					tree.get_memory().put(
							this.curent_token.get_value(),
							null
							);
					eat(IMPTokenType.VARIABLE);
					break;
				default:
					throw new IMPParserException();
			}
		}
		eat(IMPTokenType.STOP_ASSIGN_OR_DECLARATION);
	}
	
	private Expression expression() throws IOException, IMPParserException, IMPLexerException {
		return and_expression();
	}
	
	private Expression and_expression() throws IOException, IMPParserException, IMPLexerException {
		Expression ret = not_expression();
		ret.set_line_number(this.curent_token.get_line_number());
		
		while (this.curent_token.get_type() == IMPTokenType.AND) {
			eat(IMPTokenType.AND);
			
			ret = tree.new AndExpression(ret, not_expression());
			ret.set_line_number(this.curent_token.get_line_number());
		}
		return ret;
	}
	
	private Expression not_expression() throws IOException, IMPParserException, IMPLexerException {
		Expression ret = null;
	
		int counter = 0;
		while (this.curent_token.get_type() == IMPTokenType.NOT) {
			eat(IMPTokenType.NOT);
			counter++;
		}
		ret = greater_expression();
		
		while (counter > 0) {
			ret = tree.new NotExpression(ret);
			counter--;
		}
		ret.set_line_number(this.curent_token.get_line_number());
		return ret;
	}
	
	private Expression greater_expression() throws IOException, IMPParserException, IMPLexerException {
		Expression ret = additive_expression();
		ret.set_line_number(this.curent_token.get_line_number());
		
		while (this.curent_token.get_type() == IMPTokenType.GREATER) {
			eat(IMPTokenType.GREATER);
			
			ret = tree.new GreaterExpression(ret, additive_expression());
			ret.set_line_number(this.curent_token.get_line_number());
		}
		return ret;
	}
	
	private Expression additive_expression() throws IOException, IMPParserException, IMPLexerException {
		Expression ret = divide_expression();
		ret.set_line_number(this.curent_token.get_line_number());
		
		while (this.curent_token.get_type() == IMPTokenType.ADD) {
			eat(IMPTokenType.ADD);
			
			ret = tree.new AddExpression(ret, divide_expression());
			ret.set_line_number(this.curent_token.get_line_number());
		}
		return ret;
	}
	
	private Expression divide_expression() throws IOException, IMPParserException, IMPLexerException {
		Expression ret = primary();
		ret.set_line_number(this.curent_token.get_line_number());
		
		while (this.curent_token.get_type() == IMPTokenType.DIVIDE) {
			eat(IMPTokenType.DIVIDE);
			
			ret = tree.new DivideExpression(ret, primary());
			ret.set_line_number(this.curent_token.get_line_number());
		}
		return ret;
	}
	
	private Expression primary() throws IOException, IMPParserException, IMPLexerException {
		Expression ret;
		if (this.curent_token.get_type() == IMPTokenType.NUMBER) {
			ret = tree.new ArithmeticValue(
					Integer.parseInt(this.curent_token.get_value())
					);
			ret.set_line_number(this.curent_token.get_line_number());
			
			eat(IMPTokenType.NUMBER);
		}
		else if (this.curent_token.get_type() == IMPTokenType.VARIABLE) {
			ret = tree.new Variable(
					this.curent_token.get_value()
					);
			ret.set_line_number(this.curent_token.get_line_number());
			
			eat(IMPTokenType.VARIABLE);
		}
		else if (this.curent_token.get_type() == IMPTokenType.BOOLEAN) {
			ret = tree.new BooleanValue(
					(this.curent_token.get_value().equals("true")) ? true : false
					);
			ret.set_line_number(this.curent_token.get_line_number());
			
			eat(IMPTokenType.BOOLEAN);
		}
		else if (this.curent_token.get_type() == IMPTokenType.START_BRACKET) {
			eat(IMPTokenType.START_BRACKET);
			
			ret = tree.new BracketExpression(expression());
			ret.set_line_number(this.curent_token.get_line_number());
			
			eat(IMPTokenType.STOP_BRACKET);
		}
		else throw new IMPParserException();
		return ret;
	}
	
	private void eat(IMPTokenType type) throws IOException, IMPParserException, IMPLexerException {
		if (this.curent_token.get_type() == type) {
			this.curent_token = this.lexer.yylex();
		} else
			throw new IMPParserException();
	}
}
