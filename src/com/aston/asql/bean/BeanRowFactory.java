package com.aston.asql.bean;

import java.sql.SQLException;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.result.IRow;
import com.aston.asql.result.IRowFactory;

public class BeanRowFactory implements IRowFactory, IASqlBuilderAware {

	protected ASqlBuilder builder;

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
	}

	@Override
	public <T> IRow<T> createRow(Class<T> type) throws SQLException {

		BeanInfo<T> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(type);
		if (bi != null) {
			return new BeanRow<T>(bi);
		}
		return null;
	}

}
