package com.aston.asql.bean.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.base.SqlParam;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.utils.ReflectionHelper;

public class BeanSelectRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 172;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if ("bean:select".equals(recipe.expression))
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (method.getParameterTypes().length != 0 || !Void.class.equals(method.getReturnType()) || !ReflectionHelper.isList(method.getReturnType()))
			throw new SQLException(recipe.expression + " requires: List<beanType> function()");
		Class<?> rtype = ReflectionHelper.genericOneType(method.getGenericReturnType());
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException(recipe.expression + " requires: List<beanType> function(idType id), bean type is invalid " + rtype.getSimpleName());

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(bi.getTableName()).append(" order by id asc");
		recipe.sql = sb.toString();
		recipe.sqlcommand = "select";
		recipe.params = new SqlParam[0];
	}
}
