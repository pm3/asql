package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.IConverter;
import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.base.StaticSql;
import com.aston.asql.dynamic.DynamicConverter;
import com.aston.asql.dynamic.DynamicSql;
import com.aston.asql.exec.ExecInsert;
import com.aston.asql.exec.ExecSelect;
import com.aston.asql.exec.ExecUpdate;
import com.aston.asql.exec.ISqlStatement;
import com.aston.utils.ValueHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseExecRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 550;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.sql != null) {
			if ("insert".equals(recipe.sqlcommand))
				buildInsert(method, recipe);
			else if ("select".equals(recipe.sqlcommand))
				buildSelect(method, recipe);
			else
				buildUpdate(method, recipe);
		}
	}

	protected void buildSelect(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.selectResult == null)
			throw new SQLException("undefined selectresult [" + method.getReturnType().getSimpleName() + "] " + ValueHelper.baseMethodName(method) + " - " + recipe);

		ISqlStatement osql = recipe.sql.contains(DynamicConverter.DELIM) ? new DynamicSql(recipe.sql, recipe.params, builder) : new StaticSql(recipe.sql, recipe.params, builder);
		recipe.exec = new ExecSelect(osql, recipe.selectResult);
	}

	protected void buildInsert(Method method, BaseSqlRecipe recipe) throws SQLException {
		Class<?> rtype = method.getReturnType();
		IConverter c = builder.createConverter(rtype);
		if (c == null)
			throw new SQLException("undefined return type converter [" + rtype.getSimpleName() + "] " + ValueHelper.baseMethodName(method));

		ISqlStatement osql = recipe.sql.contains(DynamicConverter.DELIM) ? new DynamicSql(recipe.sql, recipe.params, builder) : new StaticSql(recipe.sql, recipe.params, builder);
		recipe.exec = new ExecInsert(osql, c, rtype);
	}

	protected void buildUpdate(Method method, BaseSqlRecipe recipe) throws SQLException {
		ISqlStatement osql = recipe.sql.contains(DynamicConverter.DELIM) ? new DynamicSql(recipe.sql, recipe.params, builder) : new StaticSql(recipe.sql, recipe.params, builder);
		recipe.exec = new ExecUpdate(osql);
	}

}
