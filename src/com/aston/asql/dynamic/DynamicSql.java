package com.aston.asql.dynamic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IConverter;
import com.aston.asql.base.SqlParam;
import com.aston.asql.exec.IPreparedStatementCreator;
import com.aston.asql.exec.ISqlStatement;
import com.aston.utils.StringHelper;

public class DynamicSql implements ISqlStatement {

	private String[] lsql;
	private SqlParam[] params;
	private ASqlBuilder builder;

	public DynamicSql(String sql, SqlParam[] params, ASqlBuilder builder) throws SQLException {
		sql = sql + " ";
		this.lsql = sql.split(DynamicConverter.DELIM);
		this.params = params;
		this.builder = builder;
		if (lsql.length != params.length + 1)
			throw new SQLException("dynamic sql has " + (lsql.length - 1) + " expressions, but " + params.length + " parameters " + sql);
	}

	@Override
	public PreparedStatement createPS(Connection c, Object[] args, IPreparedStatementCreator creator) throws SQLException {
		PreparedStatement ps = null;
		try {
			List<Object> psdata = new ArrayList<Object>();
			String ssql = createSql(args, psdata);
			ps = creator != null ? creator.createStatement(c, ssql) : c.prepareStatement(ssql);
			fillPS(ps, psdata);
		} catch (SQLException e) {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception ee) {
				}
			}
			throw e;
		}
		return ps;
	}

	protected String createSql(Object[] args, List<Object> psdata) throws SQLException {
		StringBuilder sb = new StringBuilder();
		int max = params.length;
		for (int i = 0; i < max; i++) {
			sb.append(lsql[i]);
			SqlParam p = params[i];
			try {
				Object val = args[p.pos];
				if (p.expr != null)
					val = p.expr.eval(val);
				if (p.converter instanceof DynamicConverter) {
					((DynamicConverter) p.converter).dynamicSql(sb, val, psdata);
				} else {
					sb.append("?");
					psdata.add(p.converter != null ? createFullDef(val, p.converter) : val);
				}
			} catch (Exception e) {
				throw new SQLException("parse parameter expression[" + p.name + "]:" + e.getMessage(), e);
			}
		}
		sb.append(lsql[max]);
		System.out.println(sb.toString());
		return sb.toString();
	}

	protected void fillPS(PreparedStatement ps, List<Object> psdata) throws SQLException {
		for (int i = 0; i < psdata.size(); i++) {
			Object o = psdata.get(i);
			if (o == null)
				continue;
			if (o instanceof FullDef) {
				FullDef fd = (FullDef) o;
				fd.c.bean2sql(fd.val, ps, i + 1);
			} else {
				IConverter c = builder.createConverter(o.getClass());
				if (c == null)
					throw new SQLException("dynamic param, type [" + o.getClass() + "] has undefined converter");
				c.bean2sql(o, ps, i + 1);
			}
		}
	}

	@Override
	public String toString() {
		return StringHelper.join(lsql, DynamicConverter.DELIM);
	}

	public static Object createFullDef(Object val, IConverter c) {
		return new FullDef(val, c);
	}

	static class FullDef {
		Object val;
		IConverter c;

		FullDef(Object val, IConverter c) {
			this.val = val;
			this.c = c;
		}

	}
}
