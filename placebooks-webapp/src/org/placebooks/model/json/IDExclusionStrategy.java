package org.placebooks.model.json;

import javax.persistence.Id;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class IDExclusionStrategy implements ExclusionStrategy
{

	@Override
	public boolean shouldSkipClass(final Class<?> clazz)
	{
		return false;
	}

	@Override
	public boolean shouldSkipField(final FieldAttributes f)
	{
		return f.getAnnotation(Id.class) != null;
	}
}