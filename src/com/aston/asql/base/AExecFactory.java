package com.aston.asql.base;

import java.lang.reflect.Method;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.IExecFactory;
import com.aston.asql.bean.annotation.Sql;
import com.aston.utils.ValueHelper;

public abstract class AExecFactory implements IExecFactory, IASqlBuilderAware {

	protected ASqlBuilder builder;

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
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
