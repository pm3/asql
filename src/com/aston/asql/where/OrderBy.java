package com.aston.asql.where;

public class OrderBy {

	private String field;
	private String sort;

	public OrderBy(String field, String sort) {
		this.field = field;
		this.sort = "desc".equalsIgnoreCase(sort) ? "desc" : "asc";
	}

	public void createSql(StringBuilder sb) {
		sb.append(field);
		if (sort != null)
			sb.append(" ").append(sort);
	}

}
