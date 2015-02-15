package com.aston.asql.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IPreparedStatementCreator {

	public PreparedStatement createStatement(Connection c, String sql) throws SQLException;
}
