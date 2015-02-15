package com.aston.asql;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.BaseSqlRecipe;

public interface ISqlRecipeCreator {

	public int order();

	public void build(Method method, BaseSqlRecipe recipe) throws SQLException;
}
