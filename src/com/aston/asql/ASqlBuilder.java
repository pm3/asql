package com.aston.asql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.aston.asql.base.BaseExecFactory;
import com.aston.asql.base.BaseRowFactory;
import com.aston.asql.base.BaseSelectResultFactory;
import com.aston.asql.bean.BeanExecFactory;
import com.aston.asql.bean.BeanInfoFactory;
import com.aston.asql.bean.BeanRowFactory;
import com.aston.asql.convert.BaseConverterFactory;
import com.aston.asql.handler.ASqlHandler;
import com.aston.asql.handler.FactoryHandler;

public class ASqlBuilder implements IConverterFatory {

	private IConnectionProvider provider = null;
	private List<Object> factories = new ArrayList<Object>();
	private Map<Class<?>, Object> factoryProxies = new HashMap<Class<?>, Object>();
	private Map<Object, IConverter> converters = new ConcurrentHashMap<Object, IConverter>();

	public ASqlBuilder() {
		init();
	}

	protected void init() {
		addFactory(new BaseConverterFactory());

		addFactory(new BaseExecFactory());
		addFactory(new BaseSelectResultFactory());
		addFactory(new BaseRowFactory());

		addFactory(new BeanInfoFactory());
		addFactory(new BeanExecFactory());
		addFactory(new BeanRowFactory());
	}

	public void setConnectionProvider(IConnectionProvider provider) {
		this.provider = provider;
	}

	public void addFactory(Object factory) {
		if (factory != null) {
			if (factory instanceof IASqlBuilderAware)
				((IASqlBuilderAware) factory).setASqlBuilder(this);
			factories.add(factory);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public <X> X getFactory(Class<X> type) {
		X factoryProxy = (X) factoryProxies.get(type);
		if (factoryProxy == null) {
			factoryProxy = (X) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, new FactoryHandler(factories));
			factoryProxies.put(type, factoryProxy);
		}
		return factoryProxy;
	}

	@SuppressWarnings("unchecked")
	public <X> X createMapper(Class<X> type) throws SQLException {
		ASqlHandler ih = new ASqlHandler(this, type);
		return (X) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, ih);
	}

	public void addConverter(String name, IConverter converter) {
		converters.put(name, converter);
	}

	public void addConverter(Class<?> type, IConverter converter) {
		converters.put(type, converter);
	}

	public void addConverter(Class<?> type1, Class<?> type2, IConverter converter) {
		converters.put(type1, converter);
		converters.put(type2, converter);
	}

	@Override
	public IConverter createConverter(Class<?> type) {
		IConverter c = converters.get(type);
		if (c == null) {
			c = getFactory(IConverterFatory.class).createConverter(type);
			if (c != null) {
				converters.put(type, c);
			}
		}
		return c;
	}

	public IConverter createConverter(String name) {
		return converters.get(name);
	}

	public <T> T exec(IExec<T> exec, Object[] args) throws SQLException {
		T res = null;
		Connection c = null;
		try {
			c = provider.create();
			res = exec.execSql(c, args);
			provider.close(c);
		} catch (Exception e) {
			if (c != null) {
				try {
					provider.close(c);
				} catch (Exception e2) {
				}
			}
			if (e instanceof SQLException)
				throw (SQLException) e;
			throw new SQLException(e);
		}
		return res;
	}

	private Properties properties = new Properties();

	public Properties getProperties() {
		return properties;
	}

	public void addProperty(String key, String value) throws IOException {
		if (key != null)
			properties.put(key, value);
	}

	public void addProperties(Properties p) throws IOException {
		if (p != null)
			properties.putAll(p);
	}

	public void addProperties(InputStream is) throws IOException {
		if (is != null)
			properties.load(is);
	}

	public void addProperties(File f) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			addProperties(fis);
		} catch (IOException e) {
			if (fis != null)
				fis.close();
			throw e;
		}
	}
}
