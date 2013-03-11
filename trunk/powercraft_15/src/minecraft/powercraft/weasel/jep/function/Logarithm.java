/*****************************************************************************

 JEP 2.4.1, Extensions 1.1.1
      April 30 2007
      (c) Copyright 2007, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

 *****************************************************************************/
package powercraft.weasel.jep.function;


import java.util.Stack;

import powercraft.weasel.jep.ParseException;
import powercraft.weasel.jep.type.Complex;



/**
 * Log bass 10.
 * <p>
 * RJM change return real results for positive real arguments. Speedup by using
 * static final fields.
 */
public class Logarithm extends PostfixMathCommand {
	private static final double LOG10 = Math.log(10);
	private static final Complex CLOG10 = new Complex(Math.log(10), 0);

	public Logarithm() {
		numberOfParameters = 1;
	}

	@Override
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);// check the stack
		Object param = inStack.pop();
		inStack.push(log(param));//push the result on the inStack
		return;
	}


	public Object log(Object param) throws ParseException {
		if (param instanceof Complex) {
			return ((Complex) param).log().div(CLOG10);
		} else if (param instanceof Number) {
			double num = ((Number) param).doubleValue();
			if (num >= 0)
				return new Double(Math.log(num) / LOG10);
			else if (num != num)
				return new Double(Double.NaN);
			else {
				Complex temp = new Complex(num);
				return temp.log().div(CLOG10);
			}
		}
		throw new ParseException("log() not defined for " + param.getClass().getSimpleName());
	}


}
