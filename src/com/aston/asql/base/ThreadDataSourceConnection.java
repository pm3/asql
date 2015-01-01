package com.aston.asql.base;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.aston.asql.IConnectionProvider;

public class ThreadDataSourceConnection implements IConnectionProvider {

	private DataSource ds;
	private ThreadLocal<Connection> transaction = new ThreadLocal<Connection>();

	public ThreadDataSourceConnection(DataSource ds) {
		this.ds = ds;
	}

	public void createTransaction() throws SQLException {
		Connection c = transaction.get();
		if (c != null)
			throw new SQLException("double transaction create");
		c = ds.getConnection();
		c.setAutoCommit(false);
		transaction.set(c);
	}

	public void closeTransaction(boolean commit) throws SQLException {
		Connection c = transaction.get();
		if (c == null)
			throw new SQLException("undefined transaction close");
		try {
			if (commit)
				c.commit();
			else
				c.rollback();
		} catch (SQLException e) {
			try {
				c.close();
			} catch (Exception ee) {
			}
			throw e;
		}
		try {
			c.close();
		} catch (Exception ee) {
		}
	}

	@Override
	public Connection create() throws SQLException {
		Connection c = transaction.get();
		if (c == null) {
			c = ds.getConnection();
			c.setAutoCommit(true);
		}
		return c;
	}

	@Override
	public void close(Connection c) throws SQLException {
		Connection ctr = transaction.get();
		if (ctr == null || !ctr.equals(c))
			c.close();
	}

}
