package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.exec.ExecSelect;
import com.aston.utils.ValueHelper;

public class SelectExecRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 552;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.sql != null && "select".equals(recipe.sqlcommand))
			build0(method, recipe);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.selectResult == null)
			throw new SQLException("undefined selectresult [" + method.getReturnType().getSimpleName() + "] " + ValueHelper.baseMethodName(method) + " - " + recipe);

		recipe.exec = new ExecSelect(recipe.sql, recipe.params, recipe.selectResult);
	}

}
