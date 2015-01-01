package com.aston.asql.bean;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.IConverter;
import com.aston.asql.bean.BeanInfo.BeanProp;
import com.aston.asql.bean.annotation.Column;
import com.aston.asql.bean.annotation.Table;
import com.aston.utils.ValueHelper;

public class BeanInfoFactory implements IBeanInfoFactory, IASqlBuilderAware {

	protected ASqlBuilder builder;
	private Map<Class<?>, BeanInfo<?>> _cache = new HashMap<Class<?>, BeanInfo<?>>();

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> BeanInfo<T> createBeanInfo(Class<T> type) throws SQLException {

		BeanInfo<T> bi = (BeanInfo<T>) _cache.get(type);
		if (bi == null) {
			bi = createBeanInfo0(type);
			System.out.println(bi);
			_cache.put(type, bi);
		}
		return bi;
	}

	protected <T> BeanInfo<T> createBeanInfo0(Class<T> type) throws SQLException {
		List<BeanProp> props = createBeanProperties(type);
		boolean bcamelize = camelize(type);
		String tableName = tableName(type, bcamelize);
		BeanProp id = idProp(type, props);
		for (BeanProp p : props) {
			p.setDbname(propDbName(type, p, bcamelize));
			p.setConverter(propConverter(type, p, bcamelize));
		}

		BeanInfo<T> bi = new BeanInfo<T>(type, tableName, id, props);
		return bi;
	}

	protected boolean camelize(Class<?> type) {
		boolean bcamelize = false;
		String scamelize = builder.getProperties().getProperty(type.getName() + ".camelize");
		if (scamelize != null) {
			bcamelize = scamelize.equalsIgnoreCase("true") || scamelize.equals("1");
		} else {
			Table atable = type.getAnnotation(Table.class);
			if (atable != null) {
				bcamelize = atable.camelize();
			} else {
				scamelize = builder.getProperties().getProperty("camelize");
				if (scamelize != null)
					bcamelize = scamelize.equalsIgnoreCase("true") || scamelize.equals("1");
			}
		}
		return bcamelize;
	}

	protected String tableName(Class<?> type, boolean bcamelize) {
		String tableName = null;
		tableName = builder.getProperties().getProperty(type.getName());
		if (tableName == null) {
			tableName = builder.getProperties().getProperty(type.getName() + ".table");
		}
		if (tableName == null) {
			Table atable = type.getAnnotation(Table.class);
			if (atable != null) {
				tableName = atable.name();
				if (tableName != null && tableName.isEmpty())
					tableName = null;
			}
		}
		if (tableName == null) {
			String clname = type.getSimpleName();
			clname = clname.substring(0, 1).toLowerCase() + clname.substring(1);
			tableName = bcamelize ? ValueHelper.camelize(clname) : clname;
		}
		return tableName;
	}

	protected String idName(Class<?> type) {
		String sid = builder.getProperties().getProperty(type.getName() + ".id");
		if (sid == null) {
			Table atable = type.getAnnotation(Table.class);
			if (atable != null) {
				sid = atable.id();
				if (sid != null && sid.isEmpty())
					sid = null;
			}
		}
		if (sid == null)
			sid = "id";
		return sid;
	}

	protected BeanProp idProp(Class<?> type, List<BeanProp> props) {
		String sid = idName(type);
		if (sid != null)
			for (BeanProp p : props)
				if (sid.equals(p.getName()))
					return p;
		return null;
	}

	protected String propDbName(Class<?> type, BeanProp p, boolean camelize) {

		String name = builder.getProperties().getProperty(type.getName() + "." + p.getName());
		if (name == null) {
			name = builder.getProperties().getProperty(type.getName() + "." + p.getName() + ".name");
		}
		if (name == null) {
			Column acolumn = p.getGetter().getAnnotation(Column.class);
			if (acolumn != null) {
				name = acolumn.name();
				if (name == null || name.isEmpty())
					name = null;
			}
		}
		if (name == null) {
			name = camelize ? ValueHelper.camelize(p.getName()) : p.getName();
		}
		return name;
	}

	protected IConverter propConverter(Class<?> type, BeanProp p, boolean camelize) {

		IConverter c = null;
		String sn = builder.getProperties().getProperty(type.getName() + "." + p.getName() + ".converter");
		if (sn == null) {
			Column acolumn = p.getGetter().getAnnotation(Column.class);
			if (acolumn != null) {
				sn = acolumn.converter();
				if (sn != null && sn.isEmpty())
					sn = null;
			}
		}
		if (sn != null) {
			c = builder.createConverter(sn);
		} else {
			c = builder.createConverter(p.getType());
		}
		return c;
	}

	protected List<BeanProp> createBeanProperties(Class<?> type) {
		Method[] allMethods = type.getDeclaredMethods();
		List<BeanProp> l = new ArrayList<BeanInfo.BeanProp>(allMethods.length);
		for (Method m : allMethods) {
			if (!m.getReturnType().equals(Void.class) && m.getParameterTypes().length == 0) {
				if (m.getName().equals("getClass"))
					continue;
				String mn = m.getName();
				String pn = null;
				if (mn.startsWith("get"))
					pn = mn.substring(3);
				else if (mn.startsWith("is"))
					pn = mn.substring(2);

				if (pn != null) {
					try {
						l.add(createBeanProp(type, m, pn));
					} catch (Exception e) {
						System.out.println("ignore bean property " + type.getSimpleName() + "." + pn + " " + e.getMessage());
					}
				}
			}
		}
		return l;
	}

	protected BeanProp createBeanProp(Class<?> type, Method getter, String pn) throws NoSuchMethodException {
		Method setter = type.getDeclaredMethod("set" + pn, getter.getReturnType());
		String n1 = pn.substring(0, 1).toLowerCase() + pn.substring(1);
		return new BeanProp(getter.getReturnType(), n1, getter, setter);
	}

}
