package com.aston.asql;

public interface IConverterFatory {

	public IConverter createConverter(Class<?> type);
}
