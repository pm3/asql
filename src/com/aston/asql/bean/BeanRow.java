package com.aston.asql.bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.aston.asql.bean.BeanInfo.BeanProp;
import com.aston.asql.result.IRow;

public class BeanRow<T> implements IRow<T> {

	private BeanInfo<T> beanInfo;
	private BeanProp[] columns = null;

	public BeanRow(BeanInfo<T> beanInfo) {
		this.beanInfo = beanInfo;
	}

	public BeanInfo<T> getBeanInfo() {
		return beanInfo;
	}

	@Override
	public T row(ResultSet rs, int rowNum) throws SQLException {

		if (columns == null) {
			ResultSetMetaData md = rs.getMetaData();
			this.columns = new BeanProp[md.getColumnCount()];
			for (int i = 0; i < columns.length; i++)
				columns[i] = beanInfo.getPropByDbName(md.getColumnName(i + 1));
		}

		T row = null;
		try {
			row = beanInfo.getType().newInstance();
			for (int i = 0; i < columns.length; i++) {
				BeanProp np = columns[i];
				if (np != null) {
					Object val = np.getConverter().sql2bean(rs, i + 1, np.getClass());
					if (val != null)
						np.getSetter().invoke(row, val);
				}
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException("create row error [" + beanInfo.getType() + "] " + e.getMessage(), e);
		}
		return row;
	}

	@Override
	public String toString() {
		return "BeanRow [beanInfo=" + beanInfo.getType() + "]";
	}

}