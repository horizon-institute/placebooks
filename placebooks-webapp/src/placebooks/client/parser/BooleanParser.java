package placebooks.client.parser;

import com.google.gwt.json.client.JSONValue;

public class BooleanParser implements JSONParser<Boolean>
{
	public static final BooleanParser INSTANCE = new BooleanParser();

	@Override
	public Boolean parse(final JSONValue object)
	{
		if (object == null || object.isBoolean() == null) { return null; }
		return object.isBoolean().booleanValue();
	}

	@Override
	public void write(final StringBuilder builder, final Boolean object)
	{
		builder.append(object.toString());
	}
}
