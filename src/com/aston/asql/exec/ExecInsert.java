package com.aston.asql.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.aston.asql.IConverter;
import com.aston.asql.IExec;

public class ExecInsert<T> implements IExec<T> {

	protected String sql;
	protected SqlParam[] params;
	protected IConverter converter;
	protected Class<T> returnType;

	public ExecInsert(String sql, SqlParam[] params, IConverter converter, Class<T> returnType) {
		this.sql = sql;
		this.params = params;
		this.converter = converter;
		this.returnType = returnType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T execSql(Connection c, Object[] args) throws SQLException {

		T oid = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			SqlParam.fillPs(ps, params, args);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next())
				oid = (T) converter.sql2bean(rs, 1, returnType);
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
		return oid;
	}

	@Override
	public String toString() {
		return "ExecInsert [" + sql + "]";
	}

}
