package placebooks.client.parser;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONValue;

public class LongParser implements JSONParser<Long>
{
	public static final LongParser INSTANCE = new LongParser();

	@Override
	public Long parse(final JSONValue object)
	{
		if (object == null || object.isString() == null) { return null; }
		return Long.parseLong(object.isString().stringValue());
	}

	@Override
	public void write(final StringBuilder builder, final Long object)
	{
		builder.append(JsonUtils.escapeValue(object.toString()));
	}
}
