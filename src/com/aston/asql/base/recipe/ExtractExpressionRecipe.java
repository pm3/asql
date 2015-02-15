package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.bean.annotation.Sql;
import com.aston.utils.ValueHelper;

public class ExtractExpressionRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 50;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.expression == null)
			recipe.expression = methodSql(method);
	}

	protected String methodSql(Method method) {

		String ssql = methodSqlProp(method);
		if (ssql == null) {
			ssql = methodSqlAnot(method);
		}
		return ssql;
	}

	protected String methodSqlProp(Method method) {
		String bmn = ValueHelper.baseMethodName(method);
		String ssql = builder.getProperties().getProperty(bmn);
		if (ssql == null) {
			ssql = builder.getProperties().getProperty(bmn + ".sql");
		}
		return ssql;
	}

	protected String methodSqlAnot(Method method) {
		String ssql = null;
		Sql asql = method.getAnnotation(Sql.class);
		if (asql != null) {
			ssql = asql.value();
		}
		return ssql;
	}

}
