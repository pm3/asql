package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class IntConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof Integer) {
			ps.setInt(pos, ((Integer) val).intValue());
		}
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		Object o = rs.getInt(pos);
		return type.isPrimitive() || !rs.wasNull() ? o : null;
	}

}
