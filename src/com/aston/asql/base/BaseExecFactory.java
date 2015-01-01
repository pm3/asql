package com.aston.asql.base;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aston.asql.IConverter;
import com.aston.asql.IExec;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.asql.exec.ExecInsert;
import com.aston.asql.exec.ExecSelect;
import com.aston.asql.exec.ExecUpdate;
import com.aston.asql.exec.SqlParam;
import com.aston.asql.expr.SqlExprParser;
import com.aston.asql.result.ISelectResult;
import com.aston.asql.result.ISelectResultFactory;
import com.aston.utils.ReflectionHelper;
import com.aston.utils.ValueHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BaseExecFactory extends AExecFactory {

	@Override
	public IExec<?> createExec(Method method) throws SQLException {

		String msql = methodSql(method);
		if (msql == null)
			return null;

		List<SqlParam> params = new ArrayList<SqlParam>();
		SqlExprParser p = new SqlExprParser();
		String sql = p.parse(msql, new SqlParamCreator(builder, method.getParameterTypes(), params));

		return createExec(method, sql, params.toArray(new SqlParam[params.size()]));
	}

	protected IExec<?> createExec(Method method, String sql, SqlParam[] params) throws SQLException {

		String sql0 = sql.toLowerCase();

		IExec<?> e = null;
		if (sql0.startsWith("insert")) {
			e = insertExec(method, sql, params);
		} else if (sql0.startsWith("select")) {
			e = selectExec(method, sql, params);
		} else if (sql0.startsWith("where")) {
			e = whereExec(method, sql, params);
		} else {
			// update
			e = new ExecUpdate(sql, params);
		}
		return e;
	}

	protected IExec<?> insertExec(Method method, String sql, SqlParam[] params) throws SQLException {
		Class<?> rtype = method.getReturnType();
		IConverter c = builder.createConverter(rtype);
		if (c == null)
			throw new SQLException("undefined return type converter [" + rtype.getSimpleName() + "] " + ValueHelper.baseMethodName(method));
		return new ExecInsert(sql, params, c, rtype);
	}

	protected IExec<?> selectExec(Method method, String sql, SqlParam[] params) throws SQLException {
		ISelectResult<?> result = builder.getFactory(ISelectResultFactory.class).createSelectResult(method);
		if (result == null)
			throw new SQLException("undefined selectresult [" + method.getReturnType().getSimpleName() + "] " + ValueHelper.baseMethodName(method));
		return new ExecSelect(sql, params, result);
	}

	protected IExec<?> whereExec(Method method, String sql, SqlParam[] params) throws SQLException {
		Class<?> rtype = ReflectionHelper.isList(method.getReturnType()) ? ReflectionHelper.genericOneType(method.getGenericReturnType()) : method.getReturnType();
		BeanInfo<?> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(rtype);
		if (bi == null)
			throw new SQLException("'where' function required return BeanType [" + rtype.getSimpleName() + "] " + ValueHelper.baseMethodName(method));

		ISelectResult<?> result = builder.getFactory(ISelectResultFactory.class).createSelectResult(method);
		return new ExecSelect("select * from " + bi.getTableName() + " " + sql, params, result);
	}
}
