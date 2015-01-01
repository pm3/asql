package com.aston.asql.result;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRow<T> {
	public T row(ResultSet rs, int rowNum) throws SQLException;
}
