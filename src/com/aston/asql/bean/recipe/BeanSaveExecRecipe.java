package com.aston.asql.bean.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aston.asql.IExec;
import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.BeanInfo.BeanProp;
import com.aston.asql.bean.BeanSaveExec;
import com.aston.asql.bean.GetParamExpr;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.asql.exec.ExecInsert;
import com.aston.asql.exec.ExecUpdate;
import com.aston.asql.exec.SqlParam;

public class BeanSaveExecRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 174;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if ("bean:save".equals(recipe.expression))
			build0(method, recipe);

	}

	protected void build0(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (method.getParameterTypes().length != 1 || !Void.class.equals(method.getReturnType()))
			throw new SQLException(recipe.expression + " requires: void function(beanType bean)");

		Class<?> btype = method.getParameterTypes()[0];
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(btype);
		if (bi == null || bi.getId() == null)
			throw new SQLException(recipe.expression + "requires: void function(beanType bean), bean type is invalid " + btype.getSimpleName());

		recipe.exec = new BeanSaveExec(bi.getId(), insertExec(bi), updateExec(bi));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected IExec<?> insertExec(BeanInfo<?> bi) throws SQLException {
		StringBuilder sb1 = new StringBuilder();
		List<SqlParam> params1 = new ArrayList<SqlParam>();
		sb1.append("insert into ").append(bi.getTableName()).append(" (");
		for (BeanProp bp : bi.getProps()) {
			if (bp == bi.getId())
				continue;
			sb1.append(bp.getDbName()).append(',');
			params1.add(new SqlParam(bp.getName(), 0, new GetParamExpr(bp.getGetter()), bp.getConverter()));
		}
		sb1.setLength(sb1.length() - 1);
		sb1.append(") values (");
		for (int i = 0; i < params1.size(); i++)
			sb1.append("?,");
		sb1.setLength(sb1.length() - 1);
		sb1.append(")");

		return (IExec<?>) new ExecInsert(sb1.toString(), params1.toArray(new SqlParam[params1.size()]), bi.getId().getConverter(), bi.getId().getType());
	}

	protected IExec<?> updateExec(BeanInfo<?> bi) throws SQLException {
		StringBuilder sb2 = new StringBuilder();
		List<SqlParam> params2 = new ArrayList<SqlParam>();
		sb2.append("update ").append(bi.getTableName()).append(" set ");
		for (BeanProp bp : bi.getProps()) {
			if (bp == bi.getId())
				continue;
			sb2.append(bp.getDbName()).append("=?,");
			params2.add(new SqlParam(bp.getName(), 0, new GetParamExpr(bp.getGetter()), bp.getConverter()));
		}
		sb2.setLength(sb2.length() - 1);
		sb2.append(" where id=?");
		params2.add(new SqlParam(bi.getId().getName(), 0, new GetParamExpr(bi.getId().getGetter()), bi.getId().getConverter()));

		return new ExecUpdate(sb2.toString(), params2.toArray(new SqlParam[params2.size()]));
	}

}
