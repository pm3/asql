package com.aston.asql.result;

import java.sql.SQLException;

public interface IRowFactory {

	public <T> IRow<T> createRow(Class<T> type) throws SQLException;
}
