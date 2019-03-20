package ANTLR;

public class IMPVisitor extends IMPGrammarBaseVisitor<String>{
	private int indentation_level = 0;
	
	private String indentation()
	{
		String ret = "";
		for (int index = 0; index < this.indentation_level; ++index) ret += "\t";
		return ret;
	}
	
	@Override 
	public String visitProgram(IMPGrammarParser.ProgramContext ctx) { 
		return visit(ctx.main()); 
	}

	@Override 
	public String visitMain(IMPGrammarParser.MainContext ctx) { 
		String ret = "<MainNode>\n";
		
		this.indentation_level++;
		if (ctx.statement() != null) {
			ret += visit(ctx.statement());
		}
		else 
			if (ctx.sequence_statement() != null)
				ret += visit(ctx.sequence_statement());
		this.indentation_level--;
		
		return ret;
	}

	@Override 
	public String visitAsign_statement(IMPGrammarParser.Asign_statementContext ctx) {
		String ret = indentation();
		ret += "<AssignmentNode> =\n";
		
		this.indentation_level++;
		ret += visit(ctx.variable());
		ret += visit(ctx.arithmetic_expr());
		this.indentation_level--;
		
		return ret;
	}

	@Override 
	public String visitIf_statement(IMPGrammarParser.If_statementContext ctx) {
		String ret = indentation(); 		
		ret += "<IfNode> if\n";
		
		this.indentation_level++;
		ret += visit(ctx.logical_expr());
		ret += visit(ctx.then_);
		ret += visit(ctx.else_);
		this.indentation_level--;
		
		return ret; 
	}

	@Override 
	public String visitWhile_statement(IMPGrammarParser.While_statementContext ctx) { 
		String ret = indentation(); 		
		ret += "<WhileNode> while\n";
		
		this.indentation_level++;
		ret += visit(ctx.logical_expr());
		ret += visit(ctx.block());
		this.indentation_level--;
		
		return ret; 
	}

	@Override 
	public String visitBlock(IMPGrammarParser.BlockContext ctx) { 
		String ret = indentation(); 		
		ret += "<BlockNode> {}\n";
		
		
		if (ctx.sequence_statement() != null) {
			this.indentation_level++;
			ret += visit(ctx.sequence_statement());
			this.indentation_level--;
		}
		
		return ret; 
	}

	@Override
	public String visitSequence_statement(IMPGrammarParser.Sequence_statementContext ctx) { 
		String ret = "";
		if (ctx.statement(1) == null && ctx.sequence_statement() == null)
			ret += visit(ctx.statement(0));
		else
		{
			 ret += indentation();
			 ret += "<SequenceNode>\n";
				
				this.indentation_level++;
				ret += visit(ctx.statement(0));
				if (ctx.statement(1) != null)
					ret += visit(ctx.statement(1));
				else
					if (ctx.sequence_statement() != null)
						ret += visit(ctx.sequence_statement());
				
				this.indentation_level--;
		}
		
		
		return ret; 
	}
	
	@Override
	public String visitLogical_brkt(IMPGrammarParser.Logical_brktContext ctx) {
		String ret = indentation(); 		
		ret += "<BracketNode> ()\n";
		
		this.indentation_level++;
		ret += visit(ctx.logical_expr());
		this.indentation_level--;
		
		return ret;
	}
	
	@Override 
	public String visitNot(IMPGrammarParser.NotContext ctx) { 
		String ret = indentation();
		ret += "<NotNode> !\n";
		
		this.indentation_level++;
		ret += visit(ctx.logical_expr());
		this.indentation_level--;
		
		return ret; 
	}
	
	@Override 
	public String visitCompare(IMPGrammarParser.CompareContext ctx) { 
		return visit(ctx.comparison_expr()); 
	}
	
	@Override
	public String visitAnd(IMPGrammarParser.AndContext ctx) {
		String ret = indentation();
		ret += "<AndNode> &&\n";
		
		this.indentation_level++;
		ret += visit(ctx.left);
		ret += visit(ctx.right);
		this.indentation_level--;
		
		return ret;
	}
	
	@Override 
	public String visitLogical_primary(IMPGrammarParser.Logical_primaryContext ctx) {
		return visit(ctx.bool_primary()); 
	}

	@Override 
	public String visitGreater(IMPGrammarParser.GreaterContext ctx) { 
		String ret = indentation(); 		
		ret += "<GreaterNode> >\n";
			
		this.indentation_level++;
		ret += visit(ctx.left);
		ret += visit(ctx.right);
		this.indentation_level--;

		return ret;
	}

	@Override 
	public String visitCombare_bracket(IMPGrammarParser.Combare_bracketContext ctx) { 
		String ret = indentation(); 		
		ret += "<BracketNode> ()\n";
		
		this.indentation_level++;
		ret += visit(ctx.comparison_expr());
		this.indentation_level--;
		
		return ret;
	}

	@Override 
	public String visitArth_primary(IMPGrammarParser.Arth_primaryContext ctx) { 
		return visit(ctx.numeric_entity()); 
	}

	@Override 
	public String visitDiv(IMPGrammarParser.DivContext ctx) { 
		String ret = indentation(); 
		ret += "<DivNode> /\n";
			
		this.indentation_level++;
		ret += visit(ctx.left);
		ret += visit(ctx.right);
		this.indentation_level--;
		
		return ret; 
	}

	@Override 
	public String visitArth_bracket(IMPGrammarParser.Arth_bracketContext ctx) { 
		String ret = indentation(); 		
		ret += "<BracketNode> ()\n";
		
		this.indentation_level++;
		ret += visit(ctx.arithmetic_expr());
		this.indentation_level--;
		
		return ret; 
	}

	@Override 
	public String visitPlus(IMPGrammarParser.PlusContext ctx) { 
		String ret = indentation(); 		
		ret +=  "<PlusNode> +\n";
			
		this.indentation_level++;
		ret += visit(ctx.left);
		ret += visit(ctx.right);
		this.indentation_level--;
		
		return ret; 
	}

	@Override 
	public String visitNumeric_entity(IMPGrammarParser.Numeric_entityContext ctx) {
		String ret = "";
		
		if (ctx.variable() != null) {
			ret += visit(ctx.variable());
		}
		else if (ctx.arithmetic_primary() != null) {
			ret += visit(ctx.arithmetic_primary());
		}
		return ret; 
	}
	
	
	
	@Override 
	public String visitVariable(IMPGrammarParser.VariableContext ctx) { 
		String ret = indentation(); 		
		ret += "<VariableNode> " + ctx.getText() + "\n";
		return ret; 
	}

	@Override 
	public String visitArithmetic_primary(IMPGrammarParser.Arithmetic_primaryContext ctx) { 
		String ret = indentation(); 		
		ret += "<IntNode> " + Integer.parseInt(ctx.getText()) + "\n";	
		return ret; 
	}
	
	@Override 
	public String visitBool_primary(IMPGrammarParser.Bool_primaryContext ctx) { 
		String ret = indentation(); 		
		ret += "<BoolNode> " + Boolean.parseBoolean(ctx.getText()) + "\n";	
		return ret; 
	}
}
