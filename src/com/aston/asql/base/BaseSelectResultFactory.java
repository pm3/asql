package com.aston.asql.base;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.result.IRow;
import com.aston.asql.result.IRowFactory;
import com.aston.asql.result.ISelectResult;
import com.aston.asql.result.ISelectResultFactory;
import com.aston.asql.result.Row1Result;
import com.aston.asql.result.RowListResult;
import com.aston.utils.ReflectionHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseSelectResultFactory implements ISelectResultFactory, IASqlBuilderAware {

	protected ASqlBuilder builder = null;

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
	}

	@Override
	public <T> ISelectResult<T> createSelectResult(Method method) throws SQLException {

		ISelectResult r = null;
		if (List.class.isAssignableFrom(method.getReturnType())) {
			Class<?> rtype = ReflectionHelper.genericOneType(method.getGenericReturnType());
			IRow<?> row = createRow(rtype);
			r = new RowListResult(row);
		} else {
			Class<?> rtype = method.getReturnType();
			IRow<?> row = createRow(rtype);
			r = new Row1Result(row);
		}
		return r;
	}

	public <T> IRow<T> createRow(Class<T> type) throws SQLException {

		IRow<T> row = builder.getFactory(IRowFactory.class).createRow(type);
		if (row == null)
			throw new SQLException("undefined row factory for type: " + type);
		return row;
	}
}
