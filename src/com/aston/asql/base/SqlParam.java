package com.aston.asql.base;

import com.aston.asql.IConverter;
import com.aston.asql.expr.IExprEval;

public class SqlParam {

	public String name;
	public int pos;
	public IExprEval expr;
	public IConverter converter;
	public IConverter lastConverter = null;
	public Class<?> lastConverterType = null;

	public SqlParam(String name, int pos, IExprEval expr, IConverter converter) {
		this.name = name;
		this.pos = pos;
		this.expr = expr;
		this.converter = converter;
	}

	@Override
	public String toString() {
		return "[name=" + name + ", pos=" + pos + ", expr=" + expr + ", converter=" + converter + "]";
	}

}
