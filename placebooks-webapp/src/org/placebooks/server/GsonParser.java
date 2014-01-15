package org.placebooks.server;

import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.json.DateAdapter;
import org.placebooks.model.json.GeometryAdapter;
import org.placebooks.model.json.MapInstanceCreator;
import org.placebooks.model.json.PlaceBookItemAdapter;
import org.wornchaos.parser.Parser;
import org.wornchaos.parser.gson.AnnotationExclusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;

public class GsonParser implements Parser
{
	private final Gson gson;

	private static final GsonBuilder createBuilder()
	{
		final GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
		builder.registerTypeAdapter(Date.class, new DateAdapter());
		builder.registerTypeAdapter(Geometry.class, new GeometryAdapter());
		builder.registerTypeAdapter(PlaceBookItem.class, new PlaceBookItemAdapter());
		builder.registerTypeAdapter(Map.class, new MapInstanceCreator());
		return builder;
	}
	
	public GsonParser()
	{
		gson = createBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
	}

	public GsonParser(final Class<? extends Annotation>... annotations)
	{
		gson = createBuilder().setExclusionStrategies(new AnnotationExclusionStrategy(annotations)).create();
	}

	public GsonParser(final Gson gson)
	{
		this.gson = gson;
	}

	public GsonParser(final GsonBuilder builder)
	{
		gson = builder.create();
	}

	@Override
	public <T> T parse(final Type type, final Reader reader)
	{
		return gson.fromJson(reader, type);
	}

	@Override
	public <T> T parse(final Type type, final String string)
	{
		return gson.fromJson(string, type);
	}

	@Override
	public String write(final Object object)
	{
		return gson.toJson(object);
	}
}