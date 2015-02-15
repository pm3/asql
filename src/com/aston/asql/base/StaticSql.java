package com.aston.asql.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.exec.IPreparedStatementCreator;
import com.aston.asql.exec.ISqlStatement;

public class StaticSql implements ISqlStatement {

	private String sql;
	private SqlParam[] params;
	private ASqlBuilder builder;

	public StaticSql(String sql, SqlParam[] params, ASqlBuilder builder) {
		this.sql = sql;
		this.params = params;
		this.builder = builder;
	}

	@Override
	public PreparedStatement createPS(Connection c, Object[] args, IPreparedStatementCreator creator) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = creator != null ? creator.createStatement(c, sql) : c.prepareStatement(sql);
			fillPs(ps, args);
		} catch (SQLException e) {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception ee) {
				}
			}
		}
		return ps;
	}

	protected void fillPs(PreparedStatement ps, Object[] args) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			SqlParam p = params[i];
			try {
				Object val = args[p.pos];

				if (p.expr != null) {
					val = p.expr.eval(val);
				}
				if (p.converter != null) {
					p.converter.bean2sql(val, ps, i + 1);
					continue;
				}
				if (val != null) {
					if (p.lastConverter == null || p.lastConverterType == null || !p.lastConverterType.equals(val.getClass())) {
						p.lastConverter = builder.createConverter(val.getClass());
						if (p.lastConverter == null)
							throw new SQLException("expresion param [" + p.name + "], type [" + val.getClass() + "] has undefined converter");
						p.lastConverterType = val.getClass();
					}
					p.lastConverter.bean2sql(val, ps, i + 1);
				}
			} catch (Exception e) {
				throw new SQLException("parse parameter expression[" + p.name + "]:" + e.getMessage(), e);
			}
		}
	}

	@Override
	public String toString() {
		return sql;
	}
}
