package com.aston.asql.where;

import java.util.List;

public class TwoColumnCondition implements ICondition {

	private String field1, field2;
	private String eq;

	public TwoColumnCondition(String field1, String eq, String field2) {
		this.field1 = field1;
		this.eq = " " + eq + " ";
		this.field2 = field2;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		sb.append(field1).append(eq).append(field2);
	}

	public boolean isEmpty() {
		return false;
	}
}
