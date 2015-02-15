package com.aston.asql.bean;

import java.sql.SQLException;

public interface IBeanInfoFactory {

	public <T> BeanInfo<T> createBeanInfo(Class<T> type) throws SQLException;
}
