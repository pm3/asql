package com.aston.asql;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionProvider {

	public Connection create() throws SQLException;

	public void close(Connection c) throws SQLException;
}
