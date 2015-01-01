package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class LongConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof Long) {
			ps.setLong(pos, ((Long) val).longValue());
		}
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		Object o = rs.getLong(pos);
		return type.isPrimitive() || !rs.wasNull() ? o : null;
	}

}
