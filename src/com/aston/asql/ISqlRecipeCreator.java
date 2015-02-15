package com.aston.asql;

import java.lang.reflect.Method;
import java.sql.SQLException;

public interface ISqlRecipeCreator<T> {

	public int order();

	public void build(Method method, T recipe) throws SQLException;
}
