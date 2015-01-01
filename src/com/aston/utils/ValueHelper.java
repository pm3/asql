package com.aston.utils;

import java.lang.reflect.Method;

public class ValueHelper {

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		return o1 != null && o1.equals(o2);
	}

	public static <T> boolean in(T eq, T... items) {
		if (eq == null || items == null)
			return false;
		for (T t : items)
			if (eq.equals(t))
				return true;
		return false;
	}

	public static boolean empty(String s) {
		return s == null || s.isEmpty();
	}

	public static boolean notEmpty(String s) {
		return s != null && !s.isEmpty();
	}

	public static String camelize(String s) {
		StringBuffer sb = new StringBuffer(s.length() + 10);
		char[] buf = s.toCharArray();
		for (int i = 0; i < buf.length; i++) {
			char ch = buf[i];
			if (i > 0 && Character.isUpperCase(ch))
				sb.append('_');
			sb.append(Character.toLowerCase(ch));
		}
		return sb.toString();
	}

	public static String baseMethodName(Method m) {
		return m.getDeclaringClass().getName() + "." + m.getName();
	}

}
