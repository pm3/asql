package com.aston.asql.base;

import com.aston.asql.ASqlBuilder;
import com.aston.asql.IASqlBuilderAware;
import com.aston.asql.ISqlRecipeCreator;

public abstract class ASqlRecipeCreator implements ISqlRecipeCreator, IASqlBuilderAware {

	protected ASqlBuilder builder = null;

	@Override
	public void setASqlBuilder(ASqlBuilder builder) {
		this.builder = builder;
	}
}
