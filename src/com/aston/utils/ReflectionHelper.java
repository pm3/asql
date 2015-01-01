package com.aston.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ReflectionHelper {

	public static Class<?> genericOneType(Type returnType) {
		if (returnType instanceof ParameterizedType) {
			ParameterizedType ptype = (ParameterizedType) returnType;
			Type[] typeArguments = ptype.getActualTypeArguments();
			if (typeArguments != null && typeArguments.length == 1)
				return (Class<?>) typeArguments[0];
		}
		return null;
	}

	public static boolean isList(Class<?> type) {
		return List.class.isAssignableFrom(type);
	}
}
