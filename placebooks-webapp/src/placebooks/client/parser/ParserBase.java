package placebooks.client.parser;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONValue;

public class ParserBase implements Parser
{
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, JSONParser> parsers = new HashMap<Class<?>, JSONParser>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T parse(final Class<T> clazz, final String string)
	{
		if (clazz == null || string == null) { return null; }
		if (parsers.containsKey(clazz))
		{
			final JSONValue value = com.google.gwt.json.client.JSONParser.parseStrict(string);
			final JSONParser parser = parsers.get(clazz);
			return (T) parser.parse(value);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String write(final Object object)
	{
		if (object == null) { return null; }
		if (parsers.containsKey(object.getClass()))
		{
			final StringBuilder builder = new StringBuilder();
			final JSONParser parser = parsers.get(object.getClass());
			parser.write(builder, object);
			return builder.toString();
		}
		return null;
	}

}
