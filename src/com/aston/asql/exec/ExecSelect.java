package com.aston.asql.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.IExec;
import com.aston.asql.result.ISelectResult;

public class ExecSelect<T> implements IExec<T> {

	protected ISqlStatement sqls;
	protected ISelectResult<T> selectResult;

	public ExecSelect(ISqlStatement sqls, ISelectResult<T> selectResult) {
		this.sqls = sqls;
		this.selectResult = selectResult;
	}

	@Override
	public T execSql(Connection c, Object[] args) throws SQLException {

		T res = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = sqls.createPS(c, args, null);
			rs = ps.executeQuery();
			res = selectResult.result(rs);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ee) {
				}
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
		return "ExecSelect [" + sqls + "]";
	}

}
