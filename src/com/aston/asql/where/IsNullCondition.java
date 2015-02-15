package com.aston.asql.where;

import java.util.List;

public class IsNullCondition extends AbstractNotCondition {

	private String field;

	public IsNullCondition(String field) {
		this.field = field;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (!isEmpty()) {
			sb.append(field).append(" is ").append(this.getNotSql()).append("null");
		}
	}

	public boolean isEmpty() {
		return false;
	}
}
