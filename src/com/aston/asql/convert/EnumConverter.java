package com.aston.asql.convert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IConverter;

@SuppressWarnings("rawtypes")
public class EnumConverter implements IConverter {

	public Class<Enum> type;

	public EnumConverter(Class<Enum> type) {
		this.type = type;
	}

	@Override
	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException {
		if (val instanceof Enum)
			ps.setString(pos, ((Enum) val).name());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException {
		String s = rs.getString(pos);
		return s != null ? Enum.valueOf((Class<Enum>) type, s) : null;
	}
}
