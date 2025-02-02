/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package weasel.jep.function;


import java.util.Stack;

import weasel.jep.ParseException;


public class Not extends PostfixMathCommand {
	public Not() {
		numberOfParameters = 1;

	}

	@Override
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		if (param instanceof Number) {
			int r = (((Number) param).doubleValue() == 0) ? 1 : 0;
			inStack.push(new Double(r));//push the result on the inStack
		} else if (param instanceof Boolean) {
			boolean r = !((Boolean) param).booleanValue();
			inStack.push(new Boolean(r));//push the result on the inStack
		} else
			throw new ParseException("Logical NOT not defined for " + param.getClass().getSimpleName());
		return;
	}

}
