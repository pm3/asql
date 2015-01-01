package com.aston.asql.bean;

import java.lang.reflect.Method;

import com.aston.asql.exec.IExprEval;

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
