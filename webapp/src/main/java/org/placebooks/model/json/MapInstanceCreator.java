package org.placebooks.model.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.InstanceCreator;

@SuppressWarnings("rawtypes")
public class MapInstanceCreator implements InstanceCreator<Map>
{
	@Override
	public Map createInstance(Type rawType)
	{
		return new HashMap<Object, Object>();
	}
}
