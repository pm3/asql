package com.aston.asql.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface ISqlStatement {

	public PreparedStatement createPS(Connection c, Object[] args, IPreparedStatementCreator creator) throws SQLException;
}
