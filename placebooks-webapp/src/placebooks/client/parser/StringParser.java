package placebooks.client.parser;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONValue;

public class StringParser implements JSONParser<String>
{
	public static final StringParser INSTANCE = new StringParser();

	@Override
	public String parse(final JSONValue object)
	{
		if (object == null || object.isString() == null) { return null; }
		return object.isString().stringValue();
	}

	@Override
	public void write(final StringBuilder builder, final String object)
	{
		builder.append(JsonUtils.escapeValue(object));
	}
}