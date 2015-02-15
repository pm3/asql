package com.aston.asql.where;

import java.util.List;

public class ArrayCondition implements ICondition {

	private String field;
	private String op;
	private Object[] values;

	public ArrayCondition(String field, String op, Object[] values) {
		this.field = field;
		this.op = op;
		this.values = values;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (!isEmpty()) {
			sb.append(field).append(" ").append(op).append(" (");
			for (int i = 0; i < values.length; i++) {
				sb.append("?");
				if (i < values.length - 1)
					sb.append(", ");
				params.add(values[i]);
			}
			sb.append(")");
		}
	}

	public boolean isEmpty() {
		return (values == null || values.length == 0);
	}
}
