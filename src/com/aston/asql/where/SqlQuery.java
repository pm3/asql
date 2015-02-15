package com.aston.asql.where;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SqlQuery {

	private String sql;
	private ConditionContainer where = null;
	private List<OrderByClause> orderBy = null;

	public SqlQuery(String sql) {
		this.sql = sql;
	}

	public ConditionContainer where() {
		if (where == null)
			this.where = ConditionContainer.and();
		return where;
	}

	public void addOrderBy(String field, String sort) {
		if (orderBy == null)
			this.orderBy = new ArrayList<OrderByClause>();
		this.orderBy.add(new OrderByClause(field, sort));
	}

	public void removeOrderBy() {
		this.orderBy = new ArrayList<OrderByClause>();
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

		if (asql.contains("{orderBy}")) {
			if (orderBy != null && orderBy.size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (Iterator<OrderByClause> i = orderBy.iterator(); i.hasNext();) {
					(i.next()).setSql(sb);
					if (i.hasNext())
						sb.append(", ");
				}
				asql = asql.replace("{orderBy}", sb.toString());
			} else {

			}
		}
		return asql;
	}

	protected static class OrderByClause {
		private String x_field;
		private String x_sort;

		public OrderByClause(String xfield) {
			if (xfield != null) {
				int pos = xfield.indexOf(":");
				this.x_field = (pos > 0) ? xfield.substring(0, pos) : xfield;
				this.x_sort = (pos > 0 && pos < xfield.length()) ? xfield.substring(pos + 1) : null;
			}
		}

		public OrderByClause(String field, String sort) {
			this.x_field = field;
			this.x_sort = (sort != null && sort.length() > 0) ? sort : "asc";
		}

		public void setSql(StringBuilder sb) {
			sb.append(x_field);
			if (x_sort != null)
				sb.append(" ").append(x_sort);
		}
	}
}