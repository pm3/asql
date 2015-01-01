package com.aston.asql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IExec;
import com.aston.asql.IExecFactory;

public class ASqlHandler implements InvocationHandler {

	private ASqlBuilder builder;
	private Map<Method, IExec<?>> execs = new HashMap<Method, IExec<?>>();

	public ASqlHandler(ASqlBuilder builder, Class<?> type) throws SQLException {
		this.builder = builder;
		for (Method m : type.getDeclaredMethods()) {
			IExec<?> e = builder.getFactory(IExecFactory.class).createExec(m);
			if (e != null) {
				execs.put(m, e);
				System.out.println("method " + type.getSimpleName() + "." + m.getName() + " add exec " + e);
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		IExec<?> exec = execs.get(method);
		if (exec == null)
			throw new IllegalStateException("call empty method " + baseMethodName(method));
		Object val = builder.exec(exec, args);
		if (Void.class.equals(method.getReturnType()))
			return null;
		if (val == null && method.getReturnType().isPrimitive())
			throw new IllegalStateException("sql exec return null value " + baseMethodName(method));
		return val;
	}

	protected String baseMethodName(Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}
}
