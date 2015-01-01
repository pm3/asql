package com.aston.asql.base;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.IConverter;
import com.aston.asql.result.IRow;
import com.aston.asql.result.IRowFactory;

public class BaseRowFactory implements IRowFactory, IASqlBuilderAware {

	protected ASqlBuilder builder;

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> IRow<T> createRow(Class<T> type) {

		if (type.equals(Object[].class)) {
			return (IRow<T>) arrayRow;
		}

		IConverter c = builder.createConverter(type);
		if (c != null) {
			return new Row1<T>(c, type);
		}

		return null;
	}

	public IRow<Object[]> arrayRow = new IRow<Object[]>() {
		public Object[] row(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
			int max = rs.getMetaData().getColumnCount();
			Object[] a = new Object[max];
			for (int i = 0; i < max; i++)
				a[i] = rs.getObject(i + 1);
			return a;
		};
	};

	public static class Row1<T> implements IRow<T> {

		private IConverter converter;
		private Class<T> type;

		public Row1(IConverter converter, Class<T> type) {
			this.converter = converter;
			this.type = type;
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		public T row(ResultSet rs, int rowNum) throws SQLException {
			return (T) converter.sql2bean(rs, 1, type);
		}
	}

}
