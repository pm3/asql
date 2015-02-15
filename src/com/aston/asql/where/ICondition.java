package com.aston.asql.where;

import java.util.List;

public interface ICondition {

	void createSql(StringBuilder sb, List<Object> params);

	boolean isEmpty();
}
