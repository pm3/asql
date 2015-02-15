package com.aston.asql.where;

import java.util.List;

public class BetweenCondition extends AbstractNotCondition {

	private String field;
	private Object value1, value2;

	public BetweenCondition(String field, Object value1, Object value2) {
		this.field = field;
		this.value1 = value1;
		this.value2 = value2;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (value1 != null && value2 != null) {
			sb.append("(").append(field).append(" ").append(this.getNotSql()).append("between ? and ?)");
			params.add(value1);
			params.add(value2);
		} else if (value1 != null) {
			sb.append(field).append(" >= ?");
			params.add(value1);
		} else if (value2 != null) {
			sb.append(field).append(" <= ?");
			params.add(value2);
		}
	}

	public boolean isEmpty() {
		return (value1 == null && value2 == null);
	}
}
