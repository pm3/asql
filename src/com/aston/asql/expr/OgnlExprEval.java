package com.aston.asql.expr;

import ognl.Ognl;

public class OgnlExprEval implements IExprEval {

	Object ognlExp;

	public OgnlExprEval(String expr) throws Exception {
		try {
			this.ognlExp = Ognl.parseExpression(expr);
		} catch (Exception e) {
			throw new Exception("undefined expression [" + expr + "]");
		}
	}

	@Override
	public Object eval(Object val) throws Exception {
		return Ognl.getValue(ognlExp, val);
	}

}
