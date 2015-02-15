package com.aston.asql.where;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ConditionContainer implements ICondition {

	private String op;
	private List<ICondition> items;

	public ConditionContainer(String op) {
		this.op = " " + op + " ";
		this.items = new ArrayList<ICondition>();
	}

	public ConditionContainer add(ICondition c) {
		if (c != null)
			this.items.add(c);
		return this;
	}

	public ConditionContainer andContainer() {
		ConditionContainer cc = ConditionContainer.and();
		add(cc);
		return cc;
	}

	public ConditionContainer orContainer() {
		ConditionContainer cc = ConditionContainer.or();
		add(cc);
		return cc;
	}

	public ConditionContainer like(String field, Object val) {
		if (val != null) {
			String sval = val.toString();
			sval = (sval.indexOf('*') >= 0) ? sval.replace('*', '%') : sval + "%";
			add(new Single2ValCondition(field, "like", sval.toLowerCase()));
		}
		return this;
	}

	public ConditionContainer ilike(String field, Object val) {
		if (val != null) {
			String sval = val.toString();
			sval = (sval.indexOf('*') >= 0) ? sval.replace('*', '%') : sval + "%";
			add(new Single2ValCondition(field, "ilike", sval.toLowerCase()));
		}
		return this;
	}

	public ConditionContainer notLike(String field, Object val) {
		if (val != null) {
			String sval = val.toString();
			sval = (sval.indexOf('*') >= 0) ? sval.replace('*', '%') : sval + "%";
			add(new Single2ValCondition(field, "not like", sval.toLowerCase()));
		}
		return this;
	}

	public ConditionContainer notILike(String field, Object val) {
		if (val != null) {
			String sval = val.toString();
			sval = (sval.indexOf('*') >= 0) ? sval.replace('*', '%') : sval + "%";
			add(new Single2ValCondition(field, "not ilike", sval.toLowerCase()));
		}
		return this;
	}

	public ConditionContainer eq(String field, Object val) {
		return add(new SingleValCondition(field, "=", val));
	}

	public ConditionContainer notEq(String field, Object val) {
		return add(new SingleValCondition(field, "!=", val));
	}

	public ConditionContainer gt(String field, Object val) {
		return add(new SingleValCondition(field, ">", val));
	}

	public ConditionContainer ge(String field, Object val) {
		return add(new SingleValCondition(field, ">=", val));
	}

	public ConditionContainer lt(String field, Object val) {
		return add(new SingleValCondition(field, "<", val));
	}

	public ConditionContainer le(String field, Object val) {
		return add(new SingleValCondition(field, "<=", val));
	}

	public ConditionContainer eq2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, "=", field2));
	}

	public ConditionContainer notEq2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, "!=", field2));
	}

	public ConditionContainer gt2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, ">", field2));
	}

	public ConditionContainer ge2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, ">=", field2));
	}

	public ConditionContainer lt2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, "<", field2));
	}

	public ConditionContainer le2(String field1, String field2) {
		return add(new TwoColumnCondition(field1, "<=", field2));
	}

	public ConditionContainer isNull(String field) {
		return add(new IsNullCondition(field));
	}

	public ConditionContainer isNotNull(String field) {
		return add(new IsNullCondition(field).not());
	}

	public ConditionContainer in(String field, Object... values) {
		if (values != null && values.length == 1)
			return eq(field, values[0]);
		return add(new ArrayCondition(field, "in", values));
	}

	public ConditionContainer notIn(String field, Object... values) {
		if (values != null && values.length == 1)
			return notEq(field, values[0]);
		return add(new ArrayCondition(field, "not in", values));
	}

	public void subquery(String field, String op, SqlQuery query) {
		List<Object> qparams = new ArrayList<Object>();
		String qsql = query.createSql(qparams);
		add(new CustomCondition(field, op, qsql, qparams));
	}

	public ConditionContainer between(String field, Object value1, Object value2) {
		return add(new BetweenCondition(field, value1, value2));
	}

	public ConditionContainer notBetween(String field, Object value1, Object value2) {
		return add(new BetweenCondition(field, value1, value2).not());
	}

	public ConditionContainer betweenFields(String field1, String field2, Object value) {
		return add(new BetweenFieldsCondition(field1, field2, value));
	}

	public ConditionContainer notBetweenFields(String field1, String field2, Object value) {
		return add(new BetweenFieldsCondition(field1, field2, value).not());
	}

	public ConditionContainer betweenDate(String fieldFrom, String fieldTo, Date value) {
		ConditionContainer cand = this.andContainer();
		cand.isNotNull(fieldFrom);
		cand.le(fieldFrom, value);
		cand.orContainer().ge(fieldTo, value).isNull(fieldTo);
		return this;
	}

	public ConditionContainer betweenDate(String fieldFrom, String fieldTo, Date valueFrom, Date valueTo) {
		ConditionContainer orcc = this.orContainer();
		orcc.between(fieldFrom, valueFrom, valueTo);
		orcc.between(fieldTo, valueFrom, valueTo);
		if (valueFrom != null && valueTo != null) {
			orcc.betweenFields(fieldFrom, fieldTo, valueFrom);
		}
		return this;
	}

	public void createSql(StringBuilder sb, List<Object> params) {
		if (!isEmpty()) {
			boolean isfirst = true;
			sb.append("(");
			for (Iterator<ICondition> i = this.items.iterator(); i.hasNext();) {
				ICondition c = i.next();
				if (!c.isEmpty()) {
					if (!isfirst)
						sb.append(op);
					c.createSql(sb, params);
					isfirst = false;
				}
			}
			sb.append(")");
		}
	}

	public boolean isEmpty() {
		Iterator<ICondition> i = this.items.iterator();
		while (i.hasNext()) {
			ICondition c = i.next();
			if (!c.isEmpty())
				return false;
		}
		return true;
	}

	public static ConditionContainer and() {
		return new ConditionContainer("and");
	}

	public static ConditionContainer or() {
		return new ConditionContainer("or");
	}
}
