package com.aston.asql.exec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.aston.asql.IConverter;

public class SqlParam {

	public String name;
	public int pos;
	public IExprEval expr;
	public IConverter converter;

	public SqlParam(String name, int pos, IExprEval expr, IConverter converter) {
		this.name = name;
		this.pos = pos;
		this.expr = expr;
		this.converter = converter;
	}

	public static void fillPs(PreparedStatement ps, SqlParam[] params, Object[] args) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			SqlParam p = params[i];
			try {
				Object val = args[p.pos];

				if (p.expr != null)
					val = p.expr.eval(val);
				if (p.converter != null)
					p.converter.bean2sql(val, ps, i + 1);
				else if (val != null) {
					try {
						ps.setObject(i + 1, val);
					} catch (SQLException e) {
						throw new SQLException("set param value [" + p.name + "] " + val + " " + e.getMessage(), e);
					}
				}

			} catch (Exception e) {
				throw new SQLException("parse parameter expression[" + p.name + "]:" + e.getMessage(), e);
			}
		}
	}

	@Override
	public String toString() {
		return "[name=" + name + ", pos=" + pos + ", expr=" + expr + ", converter=" + converter + "]";
	}

}
