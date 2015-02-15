package com.aston.asql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.base.recipe.BaseSelectResultRecipe;
import com.aston.asql.base.recipe.ExtractExpressionRecipe;
import com.aston.asql.base.recipe.InsertExecRecipe;
import com.aston.asql.base.recipe.ParseBraceSqlRecipe;
import com.aston.asql.base.recipe.SelectExecRecipe;
import com.aston.asql.base.recipe.UpdateExecRecipe;
import com.aston.asql.bean.BeanInfoFactory;
import com.aston.asql.bean.recipe.BeanDeleteRecipe;
import com.aston.asql.bean.recipe.BeanLoadRecipe;
import com.aston.asql.bean.recipe.BeanSaveExecRecipe;
import com.aston.asql.bean.recipe.BeanSelectRecipe;
import com.aston.asql.bean.recipe.BeanSelectResultRecipe;
import com.aston.asql.bean.recipe.BeanWhereRecipe;
import com.aston.asql.convert.BaseConverterFactory;
import com.aston.asql.handler.ASqlHandler;
import com.aston.asql.handler.FactoryHandler;

public class ASqlBuilder implements IConverterFatory {

	private IConnectionProvider provider = null;
	private List<Object> factories = new ArrayList<Object>();
	Map<Class<?>, Object> factoryProxies = new HashMap<Class<?>, Object>();
	private Map<Object, IConverter> converters = new ConcurrentHashMap<Object, IConverter>();
	private List<ISqlRecipeCreator> recipeCreators = new ArrayList<ISqlRecipeCreator>();
	private boolean sorted = false;

	public ASqlBuilder() {
		init();
	}

	protected void init() {
		addSqlRecipeCreator(new ExtractExpressionRecipe());
		addSqlRecipeCreator(new ParseBraceSqlRecipe());
		addSqlRecipeCreator(new BaseSelectResultRecipe());
		addSqlRecipeCreator(new InsertExecRecipe());
		addSqlRecipeCreator(new SelectExecRecipe());
		addSqlRecipeCreator(new UpdateExecRecipe());

		addSqlRecipeCreator(new BeanSelectResultRecipe());
		addSqlRecipeCreator(new BeanSaveExecRecipe());
		addSqlRecipeCreator(new BeanLoadRecipe());
		addSqlRecipeCreator(new BeanSelectRecipe());
		addSqlRecipeCreator(new BeanDeleteRecipe());
		addSqlRecipeCreator(new BeanWhereRecipe());

		addFactory(new BaseConverterFactory());
		addFactory(new BeanInfoFactory());
	}

	public void setConnectionProvider(IConnectionProvider provider) {
		this.provider = provider;
	}

	public void addFactory(Object fatory) {
		if (fatory != null) {
			if (fatory instanceof IASqlBuilderAware)
				((IASqlBuilderAware) fatory).setASqlBuilder(this);
			factories.add(fatory);
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

	public void addSqlRecipeCreator(ISqlRecipeCreator recipeCreator) {
		if (recipeCreator != null) {
			if (recipeCreator instanceof IASqlBuilderAware)
				((IASqlBuilderAware) recipeCreator).setASqlBuilder(this);
			recipeCreators.add(recipeCreator);
			sorted = false;
		}
	}

	public IExec<?> createExec(Method method) throws SQLException {
		if (sorted == false) {
			Collections.sort(recipeCreators, new Comparator<ISqlRecipeCreator>() {
				@Override
				public int compare(ISqlRecipeCreator o1, ISqlRecipeCreator o2) {
					return o1.order() - o2.order();
				}
			});
			sorted = true;
			for (ISqlRecipeCreator c : recipeCreators)
				System.out.println(c.getClass().getSimpleName() + " " + c.order());
		}
		BaseSqlRecipe recipe = new BaseSqlRecipe();
		for (ISqlRecipeCreator c : recipeCreators) {
			c.build(method, recipe);
			if (recipe.exec != null)
				return recipe.exec;
		}
		return null;
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
