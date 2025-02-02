package weasel.compiler;


public enum WeaselOperator {

	LET("=", false, 0, false), 
	CALL("()", true, 13, true), 
	FIELD(".", false, 13, false), 
	ADD("+", true, 10, false),
	LET_ADD("+=", false, 0, false),
	SUB("-", true, 10, false),
	LET_SUB("-=", false, 0, false),
	MUL("*", false, 11, false),
	LET_MUL("*=", false, 0, false),
	DIV("/", false, 11, false),
	LET_DIV("/=", false, 0, false),
	MOD("%", false, 11, false),
	LET_MOD("%=", false, 0, false),
	AND("&&", false, 3, false),
	BIT_AND("&", false, 6, false),
	LET_BIT_AND("&=", false, 0, false),
	OR("||", false, 2, false),
	BIT_OR("|", false, 4, false),
	LET_BIT_OR("|=", false, 0, false),
	BIT_XOR("^", false, 5, false),
	LET_BIT_XOR("^=", false, 0, false),
	SHL("<<", false, 9, false),
	LET_SHL("<<=", false, 0, false),
	SHR(">>", false, 9, false),
	LET_SHR(">>=", false, 0, false),
	NOT("!", true, -1, false, false),
	BIT_NOT("~", true, -1, false, false),
	EQUAL("==", false, 7, false),
	NOT_EQUAL("!=", false, 7, false),
	GREATER(">", false, 8, false),
	LESS("<", false, 8, false),
	GREATER_EQUAL(">=", false, 8, false),
	LESS_EQUAL("<=", false, 8, false),
	CLONE("<-", true, 0, false),
	NEW_POINTER("@", true, -1, false), 
	INDEX("[]", true, -1, true),
	INC("++", true, -1, true),
	DEC("--", true, -1, true), 
	COMMA(",", false, -1, false), 
	IF("?:", false, 1, false);
	
	public final String name;
	public final boolean prefix;
	public final int precedence;
	public final boolean suffix;
	public final boolean l2r;
	
	public String getName(Access access, ParamType paramType){
		String s="operator";
		switch(access){
		case WRITE:
			s += "{WRITE}";
			break;
		default:
			break;
		}
		switch(paramType){
		case PREFIX:
			s += name + "_";
			break;
		case SUFFIX:
			s += "_" + name;
			break;
		default:
			s += name;
			break;
		}
		return s;
	}
	
	WeaselOperator(String name, boolean prefix, int precedence, boolean suffix){
		this.name = name;
		this.prefix = prefix;
		this.precedence = precedence;
		this.suffix = suffix;
		l2r = true;
	}
	
	WeaselOperator(String name, boolean prefix, int precedence, boolean suffix, boolean l2r){
		this.name = name;
		this.prefix = prefix;
		this.precedence = precedence;
		this.suffix = suffix;
		this.l2r = l2r;
	}
	
	public static enum ParamType{
		NORMAL(1), PREFIX(0), SUFFIX(0);
		
		public final int param;
		
		ParamType(int param){
			this.param = param;
		}
		
	}
	
	public static enum Access{
		READ, WRITE
	}
	
}
