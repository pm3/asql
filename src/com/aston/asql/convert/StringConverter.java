package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class StringConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof String) {
			ps.setString(pos, ((String) val));
		}
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		return rs.getString(pos);
	}

}
