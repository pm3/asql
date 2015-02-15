package com.aston.asql.where;

import java.util.List;

public class BetweenFieldsCondition extends AbstractNotCondition {

	private String field1, field2;
	private Object value;

	public BetweenFieldsCondition(String field1, String field2, Object value) {
		this.field1 = field1;
		this.field2 = field2;
		this.value = value;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (value != null) {
			sb.append("(? ").append(this.getNotSql()).append("between ").append(field1).append(" and ").append(field2).append(")");
			params.add(value);
		}
	}

	public boolean isEmpty() {
		return (value == null);
	}
}
