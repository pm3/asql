package com.aston.asql;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface IExecFactory {

	public IExec<?> createExec(Method method) throws SQLException;
}
