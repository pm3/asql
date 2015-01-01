package com.aston.asql.result;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RowListResult<T> implements ISelectResult<List<T>> {

	private IRow<T> row;

	public RowListResult(IRow<T> row) {
		this.row = row;
	}

	@Override
	public List<T> result(ResultSet rs) throws SQLException {
		List<T> res = new ArrayList<T>();
		int rowNum = 0;
		while (rs.next()) {
			res.add(row.row(rs, ++rowNum));
		}
		return res;
	}

}
