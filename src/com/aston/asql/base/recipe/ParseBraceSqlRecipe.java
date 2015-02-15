package com.aston.asql.base.recipe;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aston.asql.base.ASqlRecipeCreator;
import com.aston.asql.base.BaseSqlRecipe;
import com.aston.asql.base.SqlParam;
import com.aston.asql.base.SqlParamCreator;
import com.aston.asql.dynamic.DynamicConverter;
import com.aston.asql.expr.SqlExprParser;
import com.aston.utils.StringHelper;

public class ParseBraceSqlRecipe extends ASqlRecipeCreator {

	@Override
	public int order() {
		return 150;
	}

	@Override
	public void build(Method method, BaseSqlRecipe recipe) throws SQLException {
		if (recipe.expression != null && recipe.sql == null)
			build0(method, recipe);
	}

	protected void build0(Method method, BaseSqlRecipe recipe) {
		List<SqlParam> params = new ArrayList<SqlParam>();
		SqlExprParser p = new SqlExprParser();
		List<String> lsql = p.parse(recipe.expression, new SqlParamCreator(builder, method.getParameterTypes(), params));

		boolean dynamic = false;
		for (SqlParam pp : params) {
			if (pp.converter instanceof DynamicConverter)
				dynamic = true;
		}
		recipe.sql = dynamic ? StringHelper.join(lsql, DynamicConverter.DELIM) : StringHelper.join(lsql, "?");
		recipe.params = params.toArray(new SqlParam[params.size()]);

		String[] sql0 = recipe.sql.trim().toLowerCase().split("\\s", 2);
		if (sql0.length > 1)
			recipe.sqlcommand = sql0[0];
	}
}
