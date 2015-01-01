package com.aston.asql.base;

import java.util.List;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IConverter;
import com.aston.asql.exec.IExprEval;
import com.aston.asql.exec.SqlParam;
import com.aston.asql.expr.IExprParamCreator;
import com.aston.asql.expr.OgnlExprEval;

public class SqlParamCreator implements IExprParamCreator {

	private ASqlBuilder builder;
	private Class<?>[] argTypes;
	private List<SqlParam> params;

	public SqlParamCreator(ASqlBuilder builder, Class<?>[] argTypes, List<SqlParam> params) {
		this.builder = builder;
		this.argTypes = argTypes;
		this.params = params;
	}

	@Override
	public void addParam(String expr, String sconverter) {
		String exprArg = expr;
		String exprSufix = null;
		int ii1 = expr.indexOf('.');
		int ii2 = expr.indexOf('[');
		if (ii1 > 0 && (ii2 <= 0 || ii2 > ii1)) {
			exprArg = expr.substring(0, ii1);
			exprSufix = expr.substring(ii1 + 1);
		}
		if (ii2 > 0 && (ii1 <= 0 || ii1 > ii2)) {
			exprArg = expr.substring(0, ii2);
			exprSufix = expr.substring(ii2 + 1);
		}
		int pos = parseNumExpr(exprArg);
		if (pos < 0)
			throw new IllegalStateException("expresiion [" + expr + "] has undefined arg " + exprArg);
		addParam(expr, exprSufix, sconverter, pos);
	}

	private void addParam(String expr, String exprSufix, String sconverter, int pos) {

		Class<?> type = argTypes[pos];
		IConverter converter = null;
		if (sconverter != null) {
			converter = builder.createConverter(sconverter);
			if (converter == null)
				throw new IllegalStateException("expresion [" + expr + "] has undefined converter " + sconverter);
		}

		if (exprSufix == null) {
			if (converter == null)
				converter = builder.createConverter(type);
			if (converter == null)
				throw new IllegalStateException("expresion [" + expr + "] has undefined converter " + sconverter);
			params.add(new SqlParam(expr, pos, null, converter));
			return;
		}

		try {
			IExprEval ee = new OgnlExprEval(exprSufix);
			params.add(new SqlParam(expr, pos, ee, null));
		} catch (Exception e) {
			throw new IllegalStateException("bad expresiion [" + expr + "] ognl=" + exprSufix);
		}
	}

	protected int parseNumExpr(String expr) {
		int pos;
		try {
			pos = Integer.parseInt(expr);
			if (pos < 1 || pos > argTypes.length)
				throw new IllegalStateException("expresion argument position not in range " + expr + " [1-" + argTypes.length + "]");
		} catch (Exception e) {
			pos = -1;
		}
		return pos - 1;
	}

}
