package com.aston.asql.result;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface ISelectResultFactory {
	public <T> ISelectResult<T> createSelectResult(Method method) throws SQLException;
}
