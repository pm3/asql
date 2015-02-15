package com.aston.asql.where;

import java.util.List;

public class SqlQuery {

	private String sql;
	private ConditionContainer where = null;

	public SqlQuery(String sql) {
		this.sql = sql;
	}

	public ConditionContainer where() {
		if (where == null)
			this.where = ConditionContainer.and();
		return where;
	}

	public String createSql(List<Object> params) {
		String asql = sql;
		if (asql.contains("{where}")) {
			if (where != null && !where.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				where.createSql(sb, params);
				asql = asql.replace("{where}", sb.toString());
			} else {
				asql = asql.replace("{where}", "1=1");
			}
		}
		return asql;
	}
}