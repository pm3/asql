package com.aston.asql.convert;

import java.util.Date;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.IConverter;
import com.aston.asql.IConverterFatory;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BaseConverterFactory implements IConverterFatory, IASqlBuilderAware {

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		builder.addConverter(int.class, Integer.class, new IntConverter());
		builder.addConverter(long.class, Long.class, new LongConverter());
		builder.addConverter(boolean.class, BooleanConverter.class, new BooleanConverter());
		builder.addConverter(double.class, Double.class, new DoubleConverter());
		builder.addConverter(String.class, new StringConverter());
		builder.addConverter(Date.class, new TimestampConverter());
		builder.addConverter(byte[].class, new ByteaConverter());
	}

	@Override
	public IConverter createConverter(Class<?> type) {
		IConverter c = null;
		if (type.isEnum()) {
			c = new EnumConverter((Class<Enum>) type);
		}
		return c;
	}

}
