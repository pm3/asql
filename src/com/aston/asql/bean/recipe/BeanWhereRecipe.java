package com.aston.asql.bean.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.utils.ReflectionHelper;
import com.aston.utils.ValueHelper;

public class BeanWhereRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 160;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.sql != null && "where".equals(recipe.sqlcommand))
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		Class<?> rtype = ReflectionHelper.isList(method.getReturnType()) ? ReflectionHelper.genericOneType(method.getGenericReturnType()) : method.getReturnType();
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException("'where' function required return BeanType [" + rtype.getSimpleName() + "] " + ValueHelper.baseMethodName(method));

		recipe.sql = "select * from " + bi.getTableName() + " " + recipe.sql;
		recipe.sqlcommand = "select";
	}
}
