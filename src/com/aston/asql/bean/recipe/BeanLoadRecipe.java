package com.aston.asql.bean.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.GetParamExpr;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.asql.exec.SqlParam;

public class BeanLoadRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 171;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if ("bean:load".equals(recipe.expression))
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (method.getParameterTypes().length != 1 || Void.class.equals(method.getReturnType()))
			throw new SQLException(recipe.expression + " requires: beanType function(idType id)");

		Class<?> rtype = method.getReturnType();
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException(recipe.expression + " requires: beanType function(idType id), bean type is invalid " + rtype.getSimpleName());
		Class<?> idType = method.getParameterTypes()[0];
		if (bi.getId() == null || !bi.getId().getType().equals(idType))
			throw new SQLException(recipe.expression + " requires: beanType function(idType id), id type is invalid " + idType.getSimpleName());

		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(bi.getTableName()).append(" where id=?");
		recipe.sql = sb.toString();
		recipe.sqlcommand = "select";

		recipe.params = new SqlParam[1];
		recipe.params[0] = new SqlParam(bi.getId().getName(), 0, new GetParamExpr(bi.getId().getGetter()), bi.getId().getConverter());
	}
}
