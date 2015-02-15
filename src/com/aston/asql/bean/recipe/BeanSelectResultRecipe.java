package com.aston.asql.bean.recipe;

import java.sql.SQLException;

import com.aston.asql.base.recipe.BaseSelectResultRecipe;
import com.aston.asql.bean.BeanInfo;
import com.aston.asql.bean.BeanRow;
import com.aston.asql.bean.IBeanInfoFactory;
import com.aston.asql.result.IRow;

public class BeanSelectResultRecipe extends BaseSelectResultRecipe {

	@Override
	public int order() {
		return 249;
	}

	@Override
	public <T> IRow<T> createRow(Class<T> type) throws SQLException {

		BeanInfo<T> bi = builder.getFactory(IBeanInfoFactory.class).createBeanInfo(type);
		return bi != null ? new BeanRow<T>(bi) : null;
	}
}
