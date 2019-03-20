package IMPInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AST {
	private Map<String, Integer> memory;
	private MainProgram root_node;
	
	
	public AST() {
		set_memory(new HashMap<>());
	}
	
	public Map<String, Integer> get_memory() {
		return memory;
	}

	public void set_memory(Map<String, Integer> memory) {
		this.memory = memory;
	}
	
	public MainProgram get_root() {
		return root_node;
	}
	
	public void set_root(MainProgram root_node) {
		this.root_node = root_node;
	}
	
	public void check_for_errors() throws IMPASTException {
		check_for_errors(root_node);
	}
	
	private void check_for_errors(Node node) throws IMPASTException {
		List<Node> childs = childs(node);
		for (Node child : childs)
			check_for_errors(child);
	}
	
	private List<Node> childs(Node node) throws IMPASTException {
		 List<Node> childs = new ArrayList<>();
		 
		 if (node instanceof MainProgram) {
			childs.add(((MainProgram) node).statement);
		 }
		 else if (node instanceof SequenceStatement) {
			childs.add(((SequenceStatement) node).first);
			childs.add(((SequenceStatement) node).second);
		}
		else if (node instanceof WhileStatement) {
			childs.add(((WhileStatement) node).expression);
			childs.add(((WhileStatement) node).statement);
		}
		else if (node instanceof IfStatement) {
			childs.add(((IfStatement) node).expression);
			childs.add(((IfStatement) node)._then);
			childs.add(((IfStatement) node)._else);
		}
		else if (node instanceof BlockStatement) {
			childs.add(((BlockStatement) node).statement);
		}
		else if (node instanceof AssignmentStatement) {
			Variable assign = ((AssignmentStatement) node).variable;
			if (!get_memory().containsKey(assign.string))
				throw new IMPASTException("UnassignedVar " + assign.get_line_number());
			childs.add(((AssignmentStatement) node).expression);
		}
		else if (node instanceof BracketExpression) {
			childs.add(((BracketExpression) node).expression);
		}
		else if (node instanceof NotExpression) {
			childs.add(((NotExpression) node).expression);
		}
		else if (node instanceof AndExpression) {
			childs.add(((AndExpression) node).first);
			childs.add(((AndExpression) node).second);
		}
		else if (node instanceof GreaterExpression) {
			childs.add(((GreaterExpression) node).first);
			childs.add(((GreaterExpression) node).second);
		}
		else if (node instanceof DivideExpression) {
			childs.add(((DivideExpression) node).first);
			childs.add(((DivideExpression) node).second);
		}
		else if (node instanceof AddExpression) {
			childs.add(((AddExpression) node).first);
			childs.add(((AddExpression) node).second);
		}
		else if (node instanceof Variable) {
			Variable var = (Variable) node;
			
			if (!get_memory().containsKey(var.string))
				throw new IMPASTException("UnassignedVar " + var.get_line_number());
			
		}
		return childs;
	}

	@SuppressWarnings("serial")
	class IMPASTException extends Exception{
		public IMPASTException(String message) {
			super(message);
		}
	}
	
	
	abstract interface Node {
		String show(int indentation_level);	
	}
	
	abstract class Expression implements Node{
		protected int line_number;
		
		public int get_line_number() {
			return line_number;
		}

		public void set_line_number(int line_number) {
			this.line_number = line_number + 1;
		}
		
		public abstract String show(int indentation_level);
		public abstract Expression compute() throws IMPASTException;		
	}

	abstract class Statement implements Node{
		private int line_number;
		
		public int get_line_number() {
			return line_number;
		}

		public void set_line_number(int line_number) {
			this.line_number = line_number + 1;
		}
		
		public abstract String show(int indentation_level);
		public abstract void interpret() throws IMPASTException ;
	}

	class ArithmeticValue extends Expression {
	    Integer number;
	    
	    public ArithmeticValue(Integer number) {
	        this.number = number;
	    }
	    
	    @Override
	    public String show(int indentation_level) {
	    	String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<IntNode> " + number + "\n";
	        return ret;
	    }

	    @Override
	    public Expression compute() throws IMPASTException {
	        return this;
	    }
	}

	class BooleanValue extends Expression {
	    boolean bool;
	    
	    public BooleanValue(boolean bool) {
	        this.bool = bool;
	    }
	    
	    @Override
	    public String show(int indentation_level) {
	    	String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<BoolNode> " + bool + "\n";
	        return ret;
	    }

	    @Override
	    public Expression compute() throws IMPASTException {
	        return this;
	    }
	}

	class Variable extends Expression {
	    String string;
	    
	    public Variable(String string) {
	        this.string = string;
	    }

	    @Override
	    public String show(int indentation_level) {
	    	String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<VariableNode> " + string + "\n";
	        return ret;
	    }

	    @Override
	    public Expression compute() throws IMPASTException {
	    	Integer value = get_memory().get(this.string);
	    	
	    	if (value == null) 
	    		throw new IMPASTException("UnassignedVar " + this.line_number);
	    	return new ArithmeticValue(value);
	    }
	}
	
	class AddExpression extends Expression {
		Expression first;
		Expression second;
	    
	    public AddExpression(Expression first, Expression second) {
	        this.first = first;
	        this.second = second;
	    }
	    
	    @Override
	    public String show(int indentation_level) {
	    	String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<PlusNode> +\n";
	        
	        ret +=  first.show(indentation_level + 1);
	        ret += second.show(indentation_level + 1);
	        
	        return ret;
	    }

	    @Override
	    public Expression compute() throws IMPASTException {
	        return new ArithmeticValue(
	        		((ArithmeticValue)  first.compute()).number 
	        		+ 
	        		((ArithmeticValue) second.compute()).number
	        		);
	    }
	}
	
	class DivideExpression extends Expression {
		Expression first;
		Expression second;
	    
	    public DivideExpression(Expression first, Expression second) {
	        this.first = first;
	        this.second = second;
	    }
	    
	    @Override
	    public String show(int indentation_level) {
	        String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<DivNode> /\n";
	        
	        ret +=  first.show(indentation_level + 1);
	        ret += second.show(indentation_level + 1);
	        
	        return ret;
	    }

	    @Override
	    public Expression compute() throws IMPASTException {
	    	
	    	Integer divisor = ((ArithmeticValue) second.compute()).number;
	        
	    	if (divisor == 0) throw new IMPASTException("DivideByZero " + this.line_number);
	    	return new ArithmeticValue(
	        		((ArithmeticValue)  first.compute()).number 
	        		/
	        		divisor
	        		);
	    }
	}

	class GreaterExpression extends Expression {
		Expression first;
		Expression second;
		
		public GreaterExpression(Expression first, Expression second) {
	        this.first = first;
	        this.second = second;
	    }

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<GreaterNode> >\n";
	        ret +=  first.show(indentation_level + 1);
	        ret += second.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public Expression compute() throws IMPASTException {
			return new BooleanValue(
	        		((ArithmeticValue)  first.compute()).number
	        		>
	        		((ArithmeticValue) second.compute()).number
	        		);
		}
	}
	
	class AndExpression extends Expression {
		Expression first;
		Expression second;
		
		public AndExpression(Expression first, Expression second) {
	        this.first = first;
	        this.second = second;
	    }

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<AndNode> &&\n";
	        ret +=  first.show(indentation_level + 1);
	        ret += second.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public Expression compute() throws IMPASTException {
			return new BooleanValue(
	        		((BooleanValue)  first.compute()).bool
	        		&&
	        		((BooleanValue) second.compute()).bool
	        		);
		}
	}

	class NotExpression extends Expression {
		Expression expression;
		
		 public NotExpression(Expression expression) {
		        this.expression = expression;
		    }

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<NotNode> !\n";
	        ret +=  expression.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public Expression compute( )throws IMPASTException  {
			return new BooleanValue(! ((BooleanValue) expression.compute()).bool);
		}
	}

	class BracketExpression extends Expression {
		Expression expression;
		
		public BracketExpression(Expression expression) {
			this.expression = expression;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<BracketNode> ()\n";
	        ret +=  expression.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public Expression compute() throws IMPASTException {
			return expression.compute();
		}
	}

	class AssignmentStatement extends Statement {
		Expression expression;
		Variable variable;
		
		public AssignmentStatement(Expression expression, Variable variable) {
			this.expression = expression;
			this.variable = variable;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<AssignmentNode> =\n";
	    	ret +=   variable.show(indentation_level + 1);
	        ret += expression.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			get_memory().put(
					variable.string, 
					((ArithmeticValue) expression.compute()).number
					);
		}
	}
	
	class BlockStatement extends Statement {
		Statement statement;
		
		public BlockStatement (Statement statement) {
			this.statement = statement;
		}
		
		public BlockStatement () {
			this(null);
		}
		
		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<BlockNode> {}\n";
	    	if (statement != null) 
	    		ret += statement.show(indentation_level + 1);
	        
	    	return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			if (statement != null) 
				statement.interpret();
		}
	}
	
	class IfStatement extends Statement {
		Expression expression;
		BlockStatement _then;
		BlockStatement _else;
		
		public IfStatement(Expression expression, BlockStatement _then, BlockStatement _else) {
			this.expression = expression;
			this._then = _then;
			this._else = _else;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<IfNode> if\n";
	    	ret += expression.show(indentation_level + 1);
	    	ret += _then.show(indentation_level + 1);
	    	ret += _else.show(indentation_level + 1);
	        
	    	return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			
			if (((BooleanValue) expression.compute()).bool) {
				_then.interpret();
			}
			else {
				_else.interpret();
			}
		}
	}
	
	class WhileStatement extends Statement {
		Expression expression;
		BlockStatement statement;
		
		public WhileStatement(Expression expression, BlockStatement statement) {
			this.expression = expression;
			this.statement = statement;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<WhileNode> while\n";
	    	ret += expression.show(indentation_level + 1);
	    	ret += statement.show(indentation_level + 1);
	        
	    	return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			while (((BooleanValue) expression.compute()).bool) {
				statement.interpret();
			}
		}
	}
	
	class SequenceStatement extends Statement {
		
		Statement first;
		Statement second;
		
		public SequenceStatement(Statement first, Statement second) {
			this.first  = first;
			this.second = second;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret += "<SequenceNode>\n";
	        ret +=  first.show(indentation_level + 1);
	        ret += second.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			first.interpret();
			second.interpret();
		}
	}
	
	class MainProgram extends Statement {
		Statement statement;
		
		public MainProgram(Statement statement) {
			this.statement = statement;
		}

		@Override
		public String show(int indentation_level) {
			String ret = "";
	    	for (int i = 0; i < indentation_level; ++i) ret += "\t";
	    	
	    	ret +="<MainNode>\n";
	        ret +=  statement.show(indentation_level + 1);
	        
	        return ret;
		}

		@Override
		public void interpret() throws IMPASTException {
			statement.interpret();
		}
	}
}
