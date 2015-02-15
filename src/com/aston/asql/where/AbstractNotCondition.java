package com.aston.asql.where;

public abstract class AbstractNotCondition implements ICondition {

	private String notSql = "";

	public ICondition not() {
		this.notSql = "not ";
		return this;
	}

	protected String getNotSql() {
		return this.notSql;
	}
}
