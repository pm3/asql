package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.aston.asql.IConverter;
import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.result.IRow;
import com.aston.asql.result.Row1Result;
import com.aston.asql.result.RowListResult;
import com.aston.utils.ReflectionHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseSelectResultRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 250;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.selectResult == null && "select".equals(recipe.sqlcommand))
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (List.class.isAssignableFrom(method.getReturnType())) {
			Class<?> rtype = ReflectionHelper.genericOneType(method.getGenericReturnType());
			IRow<?> row = createRow(rtype);
			if (row == null)
				return;
			recipe.selectResult = new RowListResult(row);
		} else {
			Class<?> rtype = method.getReturnType();
			IRow<?> row = createRow(rtype);
			if (row == null)
				return;
			recipe.selectResult = new Row1Result(row);
		}
	}

	protected <T> IRow<T> createRow(Class<T> type) throws SQLException {

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

		@Override
		public T row(ResultSet rs, int rowNum) throws SQLException {
			return (T) converter.sql2bean(rs, 1, type);
		}
	}

}
