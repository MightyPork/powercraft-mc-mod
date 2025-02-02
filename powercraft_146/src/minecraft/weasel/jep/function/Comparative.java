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


/**
 * Implements the comparative operations <, >, <=, >=, != and ==. Caverts should
 * work where arguments are Double, Complex or String for the last two only !=
 * and == work. For other types care might be needed. Complex numbers are
 * compared using a tolerance which can be set using setTolerance().
 * 
 * @author N Funk and R Morris
 * @since 2.3.0 beta 1 a bit of a rewrite to make sub classing easier, now
 *        allows Complex to be compared to Double i.e. 1+0 i == 1.
 * @since 2.3.0 beta 2 changed the internal lt,gt,le,ge,ne and eq method to
 *        return boolean. If this breaks anything use if(lt(obj1,obj2))
 *        inStack.push(new Double(1)); else inStack.push(new Double(0));
 */
public class Comparative extends PostfixMathCommand {
	protected int id;
	double tolerance;
	public static final int LT = 0;
	public static final int GT = 1;
	public static final int LE = 2;
	public static final int GE = 3;
	public static final int NE = 4;
	public static final int EQ = 5;

	/**
	 * Constructor. Sets the number of parameters to 2. Initializes the
	 * tolerance for comparing Complex values.
	 * 
	 * @param id_in The id of the comparative operator.
	 */
	public Comparative(int id_in) {
		id = id_in;
		numberOfParameters = 2;
		tolerance = 1e-6;
	}

	@Override
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);// check the stack

		Object param2 = inStack.pop();
		Object param1 = inStack.pop();
		boolean res = false;
		switch (id) {
			case LT:
				res = lt(param1, param2);
				break;
			case GT:
				res = gt(param1, param2);
				break;
			case LE:
				res = le(param1, param2);
				break;
			case GE:
				res = ge(param1, param2);
				break;
			case NE:
				res = ne(param1, param2);
				break;
			case EQ:
				res = eq(param1, param2);
				break;
		}
		if(res){
			inStack.push(new Double(1));
		}else{
			inStack.push(new Double(0));
		}
	}

	public boolean lt(Object param1, Object param2) throws ParseException {
		if ((param1 instanceof Complex) || (param2 instanceof Complex)) throw new ParseException("< not defined for complex numbers");
		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Number) param2).doubleValue();
			return (x < y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			double x = ((Boolean) param1) ? 1 : 0;
			double y = ((Boolean) param2) ? 1 : 0;
			return (x < y);
		}
		if ((param1 instanceof String) && (param2 instanceof String)) {
			String x = ((String) param1);
			String y = ((String) param2);
			return x.compareTo(y) < 0;
		}
		throw new ParseException("< not defined for " + param1.getClass().getName() + " and " + param2.getClass().getName());
	}

	public boolean gt(Object param1, Object param2) throws ParseException {
		if ((param1 instanceof Complex) || (param2 instanceof Complex)) throw new ParseException("> not defined for complex numbers");

		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Number) param2).doubleValue();
			return (x > y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			double x = ((Boolean) param1) ? 1 : 0;
			double y = ((Boolean) param2) ? 1 : 0;
			return (x > y);
		}
		if ((param1 instanceof String) && (param2 instanceof String)) {
			String x = ((String) param1);
			String y = ((String) param2);
			return x.compareTo(y) > 0;
		}
		throw new ParseException("> not defined for " + param1.getClass().getName() + " and " + param2.getClass().getName());
	}

	public boolean le(Object param1, Object param2) throws ParseException {
		if ((param1 instanceof Complex) || (param2 instanceof Complex)) throw new ParseException("<= not defined for complex numbers");

		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Number) param2).doubleValue();
			return (x <= y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			double x = ((Boolean) param1) ? 1 : 0;
			double y = ((Boolean) param2) ? 1 : 0;
			return (x <= y);
		}
		if ((param1 instanceof String) && (param2 instanceof String)) {
			String x = ((String) param1);
			String y = ((String) param2);
			return x.compareTo(y) <= 0;
		}
		throw new ParseException("<= not defined for " + param1.getClass().getName() + " and " + param2.getClass().getName());
	}

	public boolean ge(Object param1, Object param2) throws ParseException {
		if ((param1 instanceof Complex) || (param2 instanceof Complex)) throw new ParseException(">= not defined for complex numbers");

		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Number) param2).doubleValue();
			return (x >= y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			double x = ((Boolean) param1) ? 1 : 0;
			double y = ((Boolean) param2) ? 1 : 0;
			return (x >= y);
		}
		if ((param1 instanceof String) && (param2 instanceof String)) {
			String x = ((String) param1);
			String y = ((String) param2);
			return x.compareTo(y) >= 0;
		}
		throw new ParseException(">= not defined for " + param1.getClass().getName() + " and " + param2.getClass().getName());
	}

	public boolean eq(Object param1, Object param2) throws ParseException {
		if ((param1 instanceof Complex) && (param2 instanceof Complex)) {
			return ((Complex) param1).equals((Complex) param2, tolerance);
		}
		if ((param1 instanceof Complex) && (param2 instanceof Number)) {
			return ((Complex) param1).equals(new Complex((Number) param2), tolerance);
		}
		if ((param2 instanceof Complex) && (param1 instanceof Number)) {
			return ((Complex) param2).equals(new Complex((Number) param1), tolerance);
		}
		if ((param1 instanceof Number) && (param2 instanceof Number)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Number) param2).doubleValue();
			return (x == y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			boolean x = ((Boolean) param1).booleanValue();
			boolean y = ((Boolean) param2).booleanValue();
			return (x == y);
		}
		if ((param1 instanceof Number) && (param2 instanceof Boolean)) {
			double x = ((Number) param1).doubleValue();
			double y = ((Boolean) param2).booleanValue() ? 1.0 : 0.0;
			return (x == y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Number)) {
			double x = ((Boolean) param1).booleanValue() ? 1.0 : 0.0;
			double y = ((Number) param2).doubleValue();
			return (x == y);
		}
		if ((param1 instanceof Boolean) && (param2 instanceof Boolean)) {
			double x = ((Boolean) param1) ? 1 : 0;
			double y = ((Boolean) param2) ? 1 : 0;
			return (x == y);
		}
		if ((param1 instanceof String) && (param2 instanceof String)) {
			String x = ((String) param1);
			String y = ((String) param2);
			return x.compareTo(y) == 0;
		}

		// if we get to here, just use the equal function
		return param1.equals(param2);
	}

	public boolean ne(Object param1, Object param2) throws ParseException {
		return !eq(param1, param2);
	}

	/**
	 * Returns the tolerance used for comparing complex numbers
	 */
	public double getTolerance() {
		return tolerance;
	}

	/**
	 * Sets the tolerance used for comparing complex numbers
	 * 
	 * @param d
	 */
	public void setTolerance(double d) {
		tolerance = d;
	}

}
