package com.aston.asql.where;

import java.util.List;

public class SingleValCondition implements ICondition {

	private String field;
	private String op;
	private Object value;

	public SingleValCondition(String field, String op, Object value) {
		this.field = field;
		this.op = op;
		this.value = value;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (!isEmpty()) {
			sb.append(field).append(" ").append(op).append(" ").append("?");
			params.add(value);
		}
	}

	public boolean isEmpty() {
		return (value == null);
	}
}
