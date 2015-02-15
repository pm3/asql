package com.aston.asql.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aston.asql.IExec;

public class ExecUpdate implements IExec<Integer> {

	protected ISqlStatement sqls;

	public ExecUpdate(ISqlStatement sqls) {
		this.sqls = sqls;
	}

	@Override
	public Integer execSql(Connection c, Object[] args) throws SQLException {

		Integer res = null;
		PreparedStatement ps = null;
		try {
			ps = sqls.createPS(c, args, null);
			res = ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (ps != null)
				try {
					ps.close();
				} catch (Exception ee) {
				}
			throw e;
		}
		return res;
	}

	@Override
	public String toString() {
		return "ExecUpdate [" + sqls + "]";
	}

}
