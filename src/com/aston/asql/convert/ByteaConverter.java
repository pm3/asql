package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class ByteaConverter implements IConverter {

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof byte[])
			ps.setBytes(pos, (byte[]) val);
	}

	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		return rs.getBytes(pos);
	}

}
