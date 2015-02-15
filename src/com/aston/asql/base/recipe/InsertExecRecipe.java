package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.IConverter;
import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.exec.ExecInsert;
import com.aston.utils.ValueHelper;

public class InsertExecRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 551;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.sql != null && "insert".equals(recipe.sqlcommand))
			build0(method, recipe);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		Class<?> rtype = method.getReturnType();
		IConverter c = builder.createConverter(rtype);
		if (c == null)
			throw new SQLException("undefined return type converter [" + rtype.getSimpleName() + "] " + ValueHelper.baseMethodName(method));

		recipe.exec = new ExecInsert(recipe.sql, recipe.params, c, rtype);
	}
}
