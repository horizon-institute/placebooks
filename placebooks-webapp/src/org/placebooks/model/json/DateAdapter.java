package org.placebooks.model.json;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date>
{
	@Override
	public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException
	{
		return json == null ? null : new Date(json.getAsLong());
	}

	@Override
	public JsonElement serialize(final Date src, final Type typeOfSrc, final JsonSerializationContext context)
	{
		return src == null ? null : new JsonPrimitive(src.getTime());
	}
}