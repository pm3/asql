package com.aston.asql.dynamic;

import java.sql.SQLException;
import java.util.List;

import com.aston.asql.where.ICondition;

public class WhereConverter extends DynamicConverter {

	@Override
	public void dynamicSql(StringBuilder sql, Object val, List<Object> data) throws SQLException {
		if (val instanceof ICondition) {
			if (!((ICondition) val).isEmpty())
				((ICondition) val).createSql(sql, data);
			else
				sql.append(" 1=1 ");
		}

	}

}
