package placebooks.client.parser;

import java.util.Date;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONValue;

public class DateParser implements JSONParser<Date>
{
	private static final DateTimeFormat format = DateTimeFormat.getFormat("EEEE, MMMM dd, yyyy");

	public static final DateParser INSTANCE = new DateParser();

	@Override
	public Date parse(final JSONValue object)
	{
		if (object == null || object.isString() == null) { return null; }
		return format.parse(object.isString().stringValue());
	}

	@Override
	public void write(final StringBuilder builder, final Date object)
	{
		builder.append(JsonUtils.escapeValue(format.format(object)));
	}
}
