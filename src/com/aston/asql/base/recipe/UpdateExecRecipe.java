package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.exec.ExecUpdate;

public class UpdateExecRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 553;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.sql != null)
			recipe.exec = new ExecUpdate(recipe.sql, recipe.params);
	}

}
