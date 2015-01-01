package com.aston.asql.bean;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

import com.aston.asql.IConverter;

public class BeanInfo<T> {
	private Class<T> type;
	private String tableName;
	private BeanProp id;
	private List<BeanProp> props;

	public BeanInfo(Class<T> type, String tableName, BeanProp id, List<BeanProp> props) throws SQLException {
		this.type = type;
		this.tableName = tableName;
		this.id = id;
		this.props = props;
	}

	public Class<T> getType() {
		return type;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public BeanProp getId() {
		return id;
	}

	public void setId(BeanProp id) {
		this.id = id;
	}

	public List<BeanProp> getProps() {
		return props;
	}

	public BeanProp getPropByName(String name) {
		for (BeanProp bp : props)
			if (bp.name.equals(name))
				return bp;
		return null;
	}

	public BeanProp getPropByDbName(String name) {
		for (BeanProp bp : props)
			if (bp.dbname.equals(name))
				return bp;
		return null;
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

	public static class BeanProp {
		private String name;
		private String dbname;
		private Class<?> type;
		private Method getter;
		private Method setter;
		private IConverter converter;

		public BeanProp(Class<?> type, String name, Method getter, Method setter) {
			this.type = type;
			this.name = name;
			this.getter = getter;
			this.setter = setter;
		}

		public String getName() {
			return name;
		}

		public String getDbName() {
			return dbname;
		}

		public void setDbname(String dbname) {
			this.dbname = dbname;
		}

		public Class<?> getType() {
			return type;
		}

		public Method getGetter() {
			return getter;
		}

		public Method getSetter() {
			return setter;
		}

		public IConverter getConverter() {
			return converter;
		}

		public void setConverter(IConverter converter) {
			this.converter = converter;
		}

		@Override
		public String toString() {
			return "\n BeanProp [name=" + name + ", dbname=" + dbname + ", type=" + type + ", getter=" + getter + ", setter=" + setter + ", converter=" + converter + "]";
		}
	}

	@Override
	public String toString() {
		return "BeanInfo [type=" + type + ", tableName=" + tableName + ", id=" + (id != null ? id.getName() : "") + ", props=" + props + "]";
	}
}
