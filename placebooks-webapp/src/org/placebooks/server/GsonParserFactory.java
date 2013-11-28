package org.placebooks.server;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Map;

import org.placebooks.model.PlaceBookItem;
import org.placebooks.model.json.DateAdapter;
import org.placebooks.model.json.GeometryAdapter;
import org.placebooks.model.json.IgnoreExclusionStrategy;
import org.placebooks.model.json.JsonIgnore;
import org.placebooks.model.json.MapInstanceCreator;
import org.placebooks.model.json.PlaceBookItemAdapter;
import org.wornchaos.logger.Log;
import org.wornchaos.parser.Parser;
import org.wornchaos.parser.ParserFactory;
import org.wornchaos.parser.gson.AnnotationExclusionStrategy;
import org.wornchaos.parser.gson.GsonParser;
import org.wornchaos.parser.gson.ParseGroupExclusionStrategy;

import com.google.gson.GsonBuilder;
import com.vividsolutions.jts.geom.Geometry;

public class GsonParserFactory implements ParserFactory
{
	private static final Parser defaultParser = createParser();

	private static final GsonBuilder createBuilder()
	{
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new DateAdapter());
		builder.registerTypeAdapter(Geometry.class, new GeometryAdapter());
		builder.registerTypeAdapter(PlaceBookItem.class, new PlaceBookItemAdapter());
		builder.registerTypeAdapter(Map.class, new MapInstanceCreator());
		builder.setExclusionStrategies(new IgnoreExclusionStrategy());
		return builder;
	}

	private static final Parser createParser()
	{
		return new GsonParser(createBuilder().setExclusionStrategies(new AnnotationExclusionStrategy(JsonIgnore.class),
																		new ParseGroupExclusionStrategy()).create());
	}

	@Override
	public Parser create(final Class<? extends Annotation>... annotations)
	{
		final GsonBuilder builder = createBuilder();
		for (final Class<? extends Annotation> annotation : annotations)
		{
			builder.addDeserializationExclusionStrategy(new AnnotationExclusionStrategy(annotation));
			builder.addSerializationExclusionStrategy(new AnnotationExclusionStrategy(annotation));
		}
		return new GsonParser(builder);
	}

	@Override
	public Parser create(final String parseGroup)
	{
		if (parseGroup == null || parseGroup.equals("")) { return defaultParser; }

		Log.debug("Request " + parseGroup + " parser");
		return new GsonParser(createBuilder().setExclusionStrategies(new AnnotationExclusionStrategy(JsonIgnore.class),
																		new ParseGroupExclusionStrategy(parseGroup))
				.create());
	}
	
	@Override
	public Parser create()
	{
		return defaultParser;
	}	
}
