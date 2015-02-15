package com.aston.asql.bean.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.base.SqlParam;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.asql.expr.GetParamExpr;

public class BeanDeleteRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 173;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if ("bean:delete".equals(recipe.expression))
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (method.getParameterTypes().length != 1 || !Void.class.equals(method.getReturnType()))
			throw new SQLException(recipe.expression + " requires: void function(beanType bean)");

		Class<?> btype = method.getParameterTypes()[0];
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(btype);
		if (bi == null || bi.getId() == null)
			throw new SQLException(recipe.expression + "requires: void function(beanType bean), bean type is invalid " + btype.getSimpleName());

		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(bi.getTableName()).append("where id=?");
		recipe.sql = sb.toString();
		recipe.sqlcommand = "delete";

		recipe.params = new SqlParam[1];
		recipe.params[0] = new SqlParam(bi.getId().getName(), 0, new GetParamExpr(bi.getId().getGetter()), bi.getId().getConverter());
	}
}
