/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package weasel.jep.function;


import java.util.Stack;

import weasel.jep.ParseException;
import weasel.jep.type.Complex;


public class Real extends PostfixMathCommand {
	public Real() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(re(param));//push the result on the inStack
		return;
	}

	public Number re(Object param) throws ParseException {
		if (param instanceof Complex)
			return new Double(((Complex) param).re());
		else if (param instanceof Number) return ((Number) param);

		throw new ParseException("Invalid parameter type");
	}

}
