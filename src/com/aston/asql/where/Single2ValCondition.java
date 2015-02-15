package com.aston.asql.where;

import java.util.List;

public class Single2ValCondition implements ICondition {

	private String field;
	private String op;
	private Object value;

	public Single2ValCondition(String field, String op, Object value) {
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
