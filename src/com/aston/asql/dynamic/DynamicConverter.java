package com.aston.asql.dynamic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.aston.asql.IConverter;

public abstract class DynamicConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		throw new UnsupportedOperationException("DynamicConverter bean2sql");
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		throw new UnsupportedOperationException("DynamicConverter sql2bean");
	}

	public abstract void dynamicSql(StringBuilder sql, Object val, List<Object> data) throws SQLException;

	public static String DELIM = "~~~";
}
