package com.aston.asql.dynamic;

import java.sql.SQLException;
import java.util.List;

import com.aston.asql.where.OrderBy;

public class OrderByConverter extends DynamicConverter {

	@Override
	public void dynamicSql(StringBuilder sql, Object val, List<Object> data) throws SQLException {
		if (val instanceof OrderBy) {
			((OrderBy) val).createSql(sql);
		}
	}

}
