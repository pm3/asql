package com.aston.asql;

import java.sql.Connection;
import java.sql.SQLException;

public interface IExec<T> {

	public T execSql(Connection c, Object[] args) throws SQLException;
}
