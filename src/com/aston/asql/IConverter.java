package com.aston.asql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IConverter {

	public void bean2sql(Object val, PreparedStatement ps, int pos) throws SQLException;

	public Object sql2bean(ResultSet rs, int pos, Class<?> type) throws SQLException;
}
