package com.aston.asql.dynamic;

import com.aston.asql.IConverter;
import com.aston.asql.IConverterFatory;
import com.aston.asql.where.ICondition;

public class WhereConverterFactory implements IConverterFatory {

	private IConverter whereConverter = new WhereConverter();

	@Override
	public IConverter createConverter(Class<?> type) {
		if (ICondition.class.isAssignableFrom(type))
			return whereConverter;

		return null;
	}

}
