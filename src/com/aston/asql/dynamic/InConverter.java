package com.aston.asql.dynamic;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class InConverter extends DynamicConverter {

	@Override
	public void dynamicSql(StringBuilder sql, Object val, List<Object> data) throws SQLException {
		if (val == null)
			throw new SQLException("'in' condition can not be null");
		if (val.getClass().isArray()) {
			array(sql, val, data);
		} else if (Collection.class.isAssignableFrom(val.getClass())) {
			collection(sql, (Collection<?>) val, data);
		}
	}

	protected void array(StringBuilder sql, Object val, List<Object> data) throws SQLException {
		boolean first = true;
		int length = Array.getLength(val);
		for (int i = 0; i < length; i++) {
			Object o = Array.get(val, i);
			if (o == null)
				continue;
			data.add(o);
			if (first) {
				sql.append("?");
				first = false;
			} else {
				sql.append(",?");
			}
		}
		if (first)
			throw new SQLException("'in' condition can not be empty");
	}

	protected void collection(StringBuilder sql, Collection<?> c, List<Object> data) throws SQLException {
		boolean first = true;
		for (Iterator<?> it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o == null)
				continue;
			data.add(o);
			if (first) {
				sql.append("?");
				first = false;
			} else {
				sql.append(",?");
			}
		}
		if (first)
			throw new SQLException("'in' condition can not be empty");
	}

}
