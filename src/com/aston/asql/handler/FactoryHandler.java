package com.aston.asql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FactoryHandler implements InvocationHandler {

	private List<Object> factories = new ArrayList<Object>();

	public FactoryHandler(List<Object> factories) {
		this.factories = factories;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		for (int i = factories.size() - 1; i >= 0; i--) {
			Object o = factories.get(i);
			if (method.getDeclaringClass().isAssignableFrom(o.getClass())) {
				Object val = method.invoke(o, args);
				if (val != null)
					return val;
			}
		}
		return null;
	}
}
