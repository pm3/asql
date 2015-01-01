package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class DoubleConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof Boolean) {
			ps.setBoolean(pos, (Boolean) val);
		}
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		Object o = rs.getDouble(pos);
		return type.isPrimitive() || !rs.wasNull() ? o : null;
	}
}
