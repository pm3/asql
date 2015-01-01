package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.aston.asql.IConverter;

public class TimestampConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof java.util.Date)
			ps.setTimestamp(pos, new Timestamp(((java.util.Date) val).getTime()));
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		Timestamp ts = rs.getTimestamp(pos);
		return ts != null ? new java.util.Date(ts.getTime()) : null;
	}

}
