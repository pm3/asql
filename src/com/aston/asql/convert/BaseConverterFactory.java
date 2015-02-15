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
		addConverter(builder, int.class, Integer.class, new IntConverter());
		addConverter(builder, long.class, Long.class, new LongConverter());
		addConverter(builder, boolean.class, Boolean.class, new BooleanConverter());
		addConverter(builder, double.class, Double.class, new DoubleConverter());
		builder.addConverter(String.class, new StringConverter());
		builder.addConverter(Date.class, new TimestampConverter());
		builder.addConverter(byte[].class, new ByteaConverter());
	}

	public void addConverter(ASqlBuilder builder, Class<?> type1, Class<?> type2, IConverter converter) {
		builder.addConverter(type1, converter);
		builder.addConverter(type2, converter);
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
