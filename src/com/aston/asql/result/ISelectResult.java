package com.aston.asql.result;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISelectResult<T> {
	public T result(ResultSet rs) throws SQLException;
}
