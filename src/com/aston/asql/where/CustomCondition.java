package com.aston.asql.where;

import java.util.Arrays;
import java.util.List;

public class CustomCondition implements ICondition {

	private String field;
	private String eq;
	private String csql;
	private List<?> cparams;

	public CustomCondition(String field, String eq, String sql, List<?> params) {
		this.field = field;
		this.eq = eq;
		this.csql = sql;
		this.cparams = params;
	}

	public CustomCondition(String field, String eq, String sql, Object[] params) {
		this.field = field;
		this.eq = eq;
		this.csql = sql;
		this.cparams = params != null ? Arrays.asList(params) : null;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		sb.append(field).append(" ").append(eq).append(" ").append("(").append(csql).append(")");
		if (cparams != null) {
			params.addAll(cparams);
		}
	}

	public boolean isEmpty() {
		return (csql == null || csql.length() == 0);
	}
}
