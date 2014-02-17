package org.placebooks.model.json;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryAdapter implements JsonSerializer<Geometry>, JsonDeserializer<Geometry>
{
	private final Logger logger = Logger.getLogger(GeometryAdapter.class.getName());
	
	@Override
	public Geometry deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		try
		{
			return new WKTReader().read(src.getAsString());
		}
		catch (final Throwable e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
		}		
		return null;
	}

	@Override
	public JsonElement serialize(Geometry src, Type typeOfSrc, JsonSerializationContext context)
	{
		return new JsonPrimitive(src.toText());
	}
}