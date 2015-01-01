package com.aston.asql.bean;

import java.sql.Connection;
import java.sql.SQLException;

import com.aston.asql.IExec;
import com.aston.asql.bean.BeanInfo.BeanProp;

public class BeanSaveExec implements IExec<Boolean> {

	private BeanProp id;
	private IExec<?> insert;
	private IExec<?> update;

	public BeanSaveExec(BeanProp id, IExec<?> insert, IExec<?> update) {
		this.id = id;
		this.insert = insert;
		this.update = update;
	}

	@Override
	public Boolean execSql(Connection c, Object[] args) throws SQLException {
		Object obean = args[0];
		if (obean == null)
			throw new SQLException("null bean");
		Object oid = null;
		try {
			oid = id.getGetter().invoke(obean);
		} catch (Exception e) {
			throw new SQLException("bean get id " + obean + " " + e.getMessage(), e);
		}
		if (emptyId(oid)) {
			Object newId = insert.execSql(c, args);
			try {
				id.getSetter().invoke(obean, newId);
			} catch (Exception e) {
				throw new SQLException("bean set id " + obean + " " + e.getMessage(), e);
			}
		} else {
			update.execSql(c, args);
		}
		return false;
	}

	protected boolean emptyId(Object oid) {
		if (oid == null)
			return true;
		if (oid instanceof Integer && ((Integer) oid).intValue() == 0)
			return true;
		if (oid instanceof Number && ((Number) oid).longValue() == 0)
			return true;
		return false;
	}
}