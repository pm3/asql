package com.aston.asql.expr;

import java.lang.reflect.Method;

public class GetParamExpr implements IExprEval {

	private Method getter;

	public GetParamExpr(Method getter) {
		this.getter = getter;
	}

	@Override
	public Object eval(Object val) throws Exception {
		return getter.invoke(val);
	}

}
