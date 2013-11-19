package org.placebooks.model.json;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.placebooks.model.PlaceBookItem;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PlaceBookItemAdapter implements JsonSerializer<PlaceBookItem>, JsonDeserializer<PlaceBookItem>
{
	private static final String CLASSNAME = "type";
	private final Logger logger = Logger.getLogger(PlaceBookItemAdapter.class.getName());

	@Override
	public PlaceBookItem deserialize(final JsonElement src, final Type type, final JsonDeserializationContext context)
			throws JsonParseException
	{
		if (src.isJsonObject())
		{
			try
			{
				final JsonObject obj = src.getAsJsonObject();
				final String classname = obj.getAsJsonPrimitive(CLASSNAME).getAsString();
				final String fullClassName = PlaceBookItem.class.getPackage().getName() + "." + classname;
				final Class<?> clazz = PlaceBookItem.class.getClassLoader().loadClass(fullClassName);
				return context.deserialize(src, clazz);
			}
			catch (final Exception e)
			{
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return null;
	}

	@Override
	public JsonElement serialize(final PlaceBookItem src, final Type typeOfSrc, final JsonSerializationContext context)
	{
		final JsonElement elem = context.serialize(src);
		if (elem.isJsonObject())
		{
			final JsonObject obj = elem.getAsJsonObject();
			final String className = src.getClass().getSimpleName();
			obj.addProperty(CLASSNAME, className);
		}
		return elem;
	}
}