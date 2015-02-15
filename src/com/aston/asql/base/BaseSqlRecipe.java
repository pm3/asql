package com.aston.asql.base;

import java.util.Arrays;

import com.aston.asql.IExec;
import com.aston.asql.exec.SqlParam;
import com.aston.asql.result.ISelectResult;

public class BaseSqlRecipe {

	public String expression = null;
	public String sql = null;
	public String sqlcommand = null;
	public SqlParam[] params = null;

	public ISelectResult<?> selectResult = null;

	public IExec<?> exec = null;

	@Override
	public String toString() {
		return "BaseSqlRecipe [expression=" + expression + ", sql=" + sql + ", sqlcommand=" + sqlcommand + ", params=" + Arrays.toString(params) + ", selectResult=" + selectResult
				+ ", exec=" + exec + "]";
	}

}
