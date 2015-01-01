package com.aston.asql.result;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Row1Result<T> implements ISelectResult<T> {

	private IRow<T> row;

	public Row1Result(IRow<T> row) {
		this.row = row;
	}

	@Override
	public T result(ResultSet rs) throws SQLException {
		T res = null;
		if (rs.next()) {
			res = row.row(rs, 1);
		}
		return res;
	}

}
