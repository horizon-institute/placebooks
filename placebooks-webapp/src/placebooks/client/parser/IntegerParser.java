package placebooks.client.parser;

import com.google.gwt.json.client.JSONValue;

public class IntegerParser implements JSONParser<Integer>
{
	public static final IntegerParser INSTANCE = new IntegerParser();

	@Override
	public Integer parse(final JSONValue object)
	{
		if (object == null) { return null; }
		if (object.isNumber() != null) { return (int) object.isNumber().doubleValue(); }
		if (object.isString() != null) { return Integer.parseInt(object.isString().stringValue()); }
		return null;
	}

	@Override
	public void write(final StringBuilder builder, final Integer object)
	{
		builder.append(object.toString());
	}
}
