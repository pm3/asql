package com.aston.asql.bean;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aston.asql.IExec;
import com.aston.asql.base.BaseExecFactory;
import com.aston.asql.bean.BeanInfo.BeanProp;
import com.aston.asql.exec.ExecInsert;
import com.aston.asql.exec.ExecSelect;
import com.aston.asql.exec.ExecUpdate;
import com.aston.asql.exec.SqlParam;
import com.aston.asql.result.Row1Result;
import com.aston.asql.result.RowListResult;
import com.aston.utils.ReflectionHelper;
import com.aston.utils.ValueHelper;

public class BeanExecFactory extends BaseExecFactory {

	@Override
	public IExec<?> createExec(Method method) throws SQLException {
		String sql = methodSql(method);

		if (sql == null || !sql.startsWith("bean:"))
			return null;

		IExec<?> exec = null;

		if (sql.equals("bean:save")) {
			exec = beanSave(method, sql);
		} else if (sql.equals("bean:delete")) {
			exec = beanDelete(method, sql);
		} else if (sql.equals("bean:load")) {
			exec = beanLoad(method, sql);
		} else if (sql.equals("bean:select")) {
			exec = beanSelect(method, sql);
		} else {
			throw new SQLException("invalid bean method " + ValueHelper.baseMethodName(method) + " - " + sql);
		}
		return exec;
	}

	protected IExec<?> beanSave(Method m, String ssql) throws SQLException {

		if (m.getParameterTypes().length != 1 || !Void.class.equals(m.getReturnType()))
			throw new SQLException(ssql + " requires: void function(beanType bean)");

		Class<?> btype = m.getParameterTypes()[0];
		BeanInfo<?> bi = createBeanInfo(btype);
		if (bi == null || bi.getId() == null)
			throw new SQLException(ssql + "requires: void function(beanType bean), bean type is invalid " + btype.getSimpleName());

		return new BeanSaveExec(bi.getId(), insertExec(bi), updateExec(bi));
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

	protected IExec<?> beanDelete(Method m, String ssql) throws SQLException {

		if (m.getParameterTypes().length != 1 || !Void.class.equals(m.getReturnType()))
			throw new SQLException(ssql + " requires: void function(beanType bean)");

		Class<?> btype = m.getParameterTypes()[0];
		BeanInfo<?> bi = createBeanInfo(btype);
		if (bi == null || bi.getId() == null)
			throw new SQLException(ssql + "requires: void function(beanType bean), bean type is invalid " + btype.getSimpleName());

		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(bi.getTableName()).append("where id=?");
		SqlParam[] params = new SqlParam[1];
		params[0] = new SqlParam(bi.getId().getName(), 0, new GetParamExpr(bi.getId().getGetter()), bi.getId().getConverter());

		return new ExecUpdate(sb.toString(), params);
	}

	protected IExec<?> beanLoad(Method m, String ssql) throws SQLException {

		if (m.getParameterTypes().length != 1 || Void.class.equals(m.getReturnType()))
			throw new SQLException(ssql + " requires: beanType function(idType id)");

		Class<?> rtype = m.getReturnType();
		BeanInfo<?> bi = createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException(ssql + " requires: beanType function(idType id), bean type is invalid " + rtype.getSimpleName());
		Class<?> idType = m.getParameterTypes()[0];
		if (bi.getId() == null || !bi.getId().getType().equals(idType))
			throw new SQLException(ssql + " requires: beanType function(idType id), id type is invalid " + idType.getSimpleName());

		return loadExec(bi);
	}

	protected <T> IExec<T> loadExec(BeanInfo<T> bi) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(bi.getTableName()).append(" where id=?");

		SqlParam[] params = new SqlParam[1];
		params[0] = new SqlParam(bi.getId().getName(), 0, new GetParamExpr(bi.getId().getGetter()), bi.getId().getConverter());

		return new ExecSelect<T>(sb.toString(), params, new Row1Result<T>(new BeanRow<T>(bi)));
	}

	protected IExec<?> beanSelect(Method m, String ssql) throws SQLException {
		IExec<?> exec;
		if (m.getParameterTypes().length != 1 || !Void.class.equals(m.getReturnType()) || !ReflectionHelper.isList(m.getReturnType()))
			throw new SQLException(ssql + " requires: List<beanType> function(idType id)");
		Class<?> rtype = ReflectionHelper.genericOneType(m.getGenericReturnType());
		BeanInfo<?> bi = createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException(ssql + " requires: List<beanType> function(idType id), bean type is invalid " + rtype.getSimpleName());
		Class<?> idType = m.getParameterTypes()[0];
		if (bi.getId() == null || !bi.getId().getType().equals(idType))
			throw new SQLException(ssql + " requires: List<beanType> function(idType id), id type is invalid " + idType.getSimpleName());
		exec = selectAllExec(bi);
		return exec;
	}

	protected <T> IExec<List<T>> selectAllExec(BeanInfo<T> bi) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ").append(bi.getTableName()).append(" order by id asc");
		SqlParam[] params = new SqlParam[0];

		return new ExecSelect<List<T>>(sb.toString(), params, new RowListResult<T>(new BeanRow<T>(bi)));
	}

	protected <T> BeanInfo<T> createBeanInfo(Class<T> rtype) throws SQLException {
		BeanInfo<T> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(rtype);
		return bi;
	}
}
