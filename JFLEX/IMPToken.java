package IMPInterpreter;

enum IMPTokenType {
	START_DECLARATION,
	NUMBER,
	BOOLEAN,
	VARIABLE,
	START_BRACKET,
	STOP_BRACKET,
	ADD,
	DIVIDE,
	AND,
	GREATER,
	NOT,
	START_BLOCK,
	STOP_BLOCK,
	IF,
	ELSE,
	WHILE,
	START_ASSIGN,
	STOP_ASSIGN_OR_DECLARATION,
	EOF
}

public class IMPToken {	
	private IMPTokenType type;
	private int line_number;
	private String value;
	
	public IMPToken(IMPTokenType type, int line_number, String value) {
		this.type = type;
		this.line_number = line_number;
		this.value = value;
	}
	
	public IMPToken(IMPTokenType type, int line_number) {
		this(type, line_number, null);
	}
	
	public IMPTokenType get_type() {
		return type;
	}
	
	public void set_type(IMPTokenType type) {
		this.type = type;
	}
	
	public int get_line_number() {
		return line_number;
	}
	
	public void set_line_number(int line_number) {
		this.line_number = line_number;
	}
	
	public String get_value() {
		return value;
	}
	
	public void set_value(String value) {
		this.value = value;
	}
}
