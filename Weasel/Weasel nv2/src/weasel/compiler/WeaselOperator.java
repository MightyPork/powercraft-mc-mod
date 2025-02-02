package weasel.compiler;

import java.util.HashMap;


public class WeaselOperator {

	public static final HashMap<String, Properties> operators = new HashMap<String, Properties>();
	public static final boolean unknown=false;
	public static class Properties{
		public int priority;
		public String operator, fullName;
		public Properties prefix, infix, suffix;
		public boolean l2r;
		public boolean isCommutative, isSimplifyPossible;
		
		public Properties(String operator, String fullName,
				int priority, Properties prefix, Properties infix, Properties suffix, boolean l2r,
				boolean isCommutative, boolean simplifyPossible) {
			this.operator = operator;
			this.fullName = fullName;
			this.priority = priority;
			this.prefix = prefix;
			this.infix = infix;
			this.suffix = suffix;
			this.l2r = l2r;
			this.isCommutative = isCommutative;
			this.isSimplifyPossible = simplifyPossible;
		}

		@Override
		public String toString() {
			return operator;
		}
		
		
	}
	public static final int prioRange;
	
																//		operator,	fullName	, prio  ,	prefix	,	infix	,	suffix	,	l2r		,commutative,	simplify
	public static final Properties COMMA =				new Properties(","		,"comma"		,	0	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties ASSIGN =				new Properties("="		,"assign"		,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties PLUS_ASSIGN =		new Properties("+="		,"plus assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties MINUS_ASSIGN =		new Properties("-="		,"minus assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties TIMES_ASSIGN =		new Properties("*="		,"times assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties DIVIDE_ASSIGN =		new Properties("/="		,"divide assign",	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties MOD_ASSIGN =			new Properties("%="		,"mod assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties AND_ASSIGN =			new Properties("&="		,"and assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties OR_ASSIGN =			new Properties("|="		,"or assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties XOR_ASSIGN =			new Properties("^="		,"xor assign"	,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties LSHIFT_ASSIGN =		new Properties("<<="	,"lshift assign",	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties RSHIFT_ASSIGN =		new Properties(">>="	,"rshift assign",	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties COPY =				new Properties("<:"		,"copy"			,	2	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties CONDITIONAL =		new Properties("?:"		,"conditional"	,	3	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties LOGICAL_OR =			new Properties("||"		,"logical or"	,	4	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties LOGICAL_AND =		new Properties("&&"		,"logical and"	,	5	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties BITWISE_OR =			new Properties("|"		,"bitwise or"	,	6	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties BITWISE_XOR =		new Properties("^"		,"bitwise xor"	,	7	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties BITWISE_AND =		new Properties("&"		,"bitwise and"	,	8	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties VERY_SAME =			new Properties("=="		,"very same"	,	9	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties NOT_VERY_SAME =		new Properties("!="		,"not very same",	9	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties EQUAL =				new Properties("==="	,"equal"		,	9	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties NOT_EQUAL =			new Properties("!=="	,"not equal"	,	9	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties COMPARE =			new Properties("<=>"	,"compare"		,	9	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties GREATER =			new Properties(">"		,"greater"		,	10	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties LESS =				new Properties("<"		,"less"			,	10	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties GREATER_EQUAL =		new Properties(">="		,"greater equal",	10	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties LESS_EQUAL =			new Properties("<="		,"less equal"	,	10	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties LSHIFT =				new Properties("<<"		,"shift left"	,	11	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties RSHIFT =				new Properties(">>"		,"shift right"	,	11	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties PLUS =				new Properties("+"		,"plus"			,	12	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties MINUS =				new Properties("-"		,"minus"		,	12	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties TIMES =				new Properties("*"		,"times"		,	13	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties DIVIDE =				new Properties("/"		,"divide"		,	13	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties REMAINDER =			new Properties("%"		,"remainder"	,	13	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties COPY_PREFIX =		new Properties("<:"		,"copy"			,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties NOT =				new Properties("!"		,"not"			,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties BITWISE_NOT =		new Properties("~"		,"bitwise not"	,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties MINUS_PREFIX =		new Properties("-"		,"minus"		,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties PLUS_PREFIX =		new Properties("+"		,"plus"			,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties DECREASE_PREFIX =	new Properties("--"		,"decrease"		,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties INCREASE_PREFIX =	new Properties("++"		,"increase"		,	15	,	null	,	null	,	null	,	false	,	unknown	,	unknown	);
	public static final Properties ELEMENT =			new Properties("."		,"element"		,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties ARRAY =				new Properties("[]"		,"array"		,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties CALL =				new Properties("%s"		,"call"			,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties CAST =				new Properties("()"		,"cast"			,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties DECREASE =			new Properties("--"		,"decrease"		,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties INCREASE =			new Properties("++"		,"increase"		,	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties BRACKETS =			new Properties("()"		,"brackets"		,	-1	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	public static final Properties INSTANCEOF = 		new Properties("instanceof","instanceof",	16	,	null	,	null	,	null	,	true	,	unknown	,	unknown	);
	
	static {
		COMMA.infix = COMMA;
		ASSIGN.infix = ASSIGN;
		PLUS_ASSIGN.infix = PLUS_ASSIGN;
		MINUS_ASSIGN.infix = MINUS_ASSIGN;
		TIMES_ASSIGN.infix = TIMES_ASSIGN;
		DIVIDE_ASSIGN.infix = DIVIDE_ASSIGN;
		MOD_ASSIGN.infix = MOD_ASSIGN;
		AND_ASSIGN.infix = AND_ASSIGN;
		OR_ASSIGN.infix = OR_ASSIGN;
		XOR_ASSIGN.infix = XOR_ASSIGN;
		LSHIFT_ASSIGN.infix = LSHIFT_ASSIGN;
		RSHIFT_ASSIGN.infix = RSHIFT_ASSIGN;
		COPY.infix = COPY;
		COPY.prefix = COPY_PREFIX;
		CONDITIONAL.infix = CONDITIONAL;
		LOGICAL_OR.infix = LOGICAL_OR;
		LOGICAL_AND.infix = LOGICAL_AND;
		BITWISE_OR.infix = BITWISE_OR;
		BITWISE_XOR.infix = BITWISE_XOR;
		BITWISE_AND.infix = BITWISE_AND;
		VERY_SAME.infix = VERY_SAME;
		NOT_VERY_SAME.infix = NOT_VERY_SAME;
		EQUAL.infix = EQUAL;
		NOT_EQUAL.infix = NOT_EQUAL;
		COMPARE.infix = COMPARE;
		GREATER.infix = GREATER;
		LESS.infix = LESS;
		GREATER_EQUAL.infix = GREATER_EQUAL;
		LESS_EQUAL.infix = LESS_EQUAL;
		LSHIFT.infix = LSHIFT;
		RSHIFT.infix = RSHIFT;
		PLUS.infix = PLUS;
		PLUS.prefix = PLUS_PREFIX;
		MINUS.infix = MINUS;
		MINUS.prefix = MINUS_PREFIX;
		TIMES.infix = TIMES;
		DIVIDE.infix = DIVIDE;
		REMAINDER.infix = REMAINDER;
		COPY_PREFIX.prefix = COPY_PREFIX;
		NOT.prefix = NOT;
		BITWISE_NOT.prefix = BITWISE_NOT;
		MINUS_PREFIX.prefix = MINUS_PREFIX;
		MINUS_PREFIX.infix = MINUS;
		PLUS_PREFIX.prefix = PLUS_PREFIX;
		PLUS_PREFIX.infix = PLUS;
		DECREASE_PREFIX.prefix = DECREASE_PREFIX;
		DECREASE_PREFIX.suffix = DECREASE;
		INCREASE_PREFIX.prefix = INCREASE_PREFIX;
		INCREASE_PREFIX.suffix = INCREASE;
		ELEMENT.infix = ELEMENT;
		ARRAY.prefix = ARRAY;
		ARRAY.suffix = ARRAY;
		CALL.suffix = CALL;
		CAST.prefix = CAST;
		DECREASE.suffix = DECREASE;
		DECREASE.prefix = DECREASE_PREFIX;
		INCREASE.suffix = INCREASE;
		INCREASE.prefix = INCREASE_PREFIX;
		INSTANCEOF.suffix = INSTANCEOF;
		operators.put(ASSIGN.operator, ASSIGN);
		operators.put(PLUS_ASSIGN.operator, PLUS_ASSIGN);
		operators.put(MINUS_ASSIGN.operator, MINUS_ASSIGN);
		operators.put(TIMES_ASSIGN.operator, TIMES_ASSIGN);
		operators.put(DIVIDE_ASSIGN.operator, DIVIDE_ASSIGN);
		operators.put(MOD_ASSIGN.operator, MOD_ASSIGN);
		operators.put(AND_ASSIGN.operator, AND_ASSIGN);
		operators.put(OR_ASSIGN.operator, OR_ASSIGN);
		operators.put(XOR_ASSIGN.operator, XOR_ASSIGN);
		operators.put(LSHIFT_ASSIGN.operator, LSHIFT_ASSIGN);
		operators.put(RSHIFT_ASSIGN.operator, RSHIFT_ASSIGN);
		operators.put(COPY.operator, COPY);
		//operators.put(CONDITIONAL.operator, CONDITIONAL);
		operators.put(LOGICAL_OR.operator, LOGICAL_OR);
		operators.put(LOGICAL_AND.operator, LOGICAL_AND);
		operators.put(BITWISE_OR.operator, BITWISE_OR);
		operators.put(BITWISE_XOR.operator, BITWISE_XOR);
		operators.put(BITWISE_AND.operator, BITWISE_AND);
		operators.put(VERY_SAME.operator, VERY_SAME);
		operators.put(NOT_VERY_SAME.operator, NOT_VERY_SAME);
		operators.put(EQUAL.operator, EQUAL);
		operators.put(NOT_EQUAL.operator, NOT_EQUAL);
		operators.put(COMPARE.operator, COMPARE);
		operators.put(GREATER.operator, GREATER);
		operators.put(LESS.operator, LESS);
		operators.put(GREATER_EQUAL.operator, GREATER_EQUAL);
		operators.put(LESS_EQUAL.operator, LESS_EQUAL);
		operators.put(LSHIFT.operator, LSHIFT);
		operators.put(RSHIFT.operator, RSHIFT);
		operators.put(PLUS_PREFIX.operator, PLUS_PREFIX);
		operators.put(MINUS.operator, MINUS);
		operators.put(TIMES.operator, TIMES);
		operators.put(DIVIDE.operator, DIVIDE);
		operators.put(REMAINDER.operator, REMAINDER);
		operators.put(COPY_PREFIX.operator, COPY_PREFIX);
		operators.put(NOT.operator, NOT);
		operators.put(BITWISE_NOT.operator, BITWISE_NOT);
		operators.put(MINUS_PREFIX.operator, MINUS_PREFIX);
		operators.put(PLUS.operator, PLUS);
		operators.put(DECREASE_PREFIX.operator, DECREASE_PREFIX);
		operators.put(INCREASE_PREFIX.operator, INCREASE_PREFIX);
		operators.put(ELEMENT.operator, ELEMENT);
		//operators.put(ARRAY.operator, ARRAY);
		//operators.put(CALL.operator, CALL);
		//operators.put(CAST.operator, CAST);
		operators.put(DECREASE.operator, DECREASE);
		operators.put(INCREASE.operator, INCREASE);

		int max = 0, tmpPrio;
		for (Properties prio : operators.values()) {
			tmpPrio = prio.priority;
			if (tmpPrio > max)
				max = tmpPrio;
		}
		prioRange = max;
	}
	
}
