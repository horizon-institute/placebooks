package placebooks.client.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONValue;

public class IterableParser<T> implements JSONParser<Iterable<T>>
{
	private JSONParser<T> parser;

	public IterableParser(final JSONParser<T> parser)
	{
		this.parser = parser;
	}

	@Override
	public Iterable<T> parse(final JSONValue object)
	{
		if (object == null || object.isArray() == null) { return null; }
		final List<T> list = new ArrayList<T>();

		for (int index = 0; index < object.isArray().size(); index++)
		{
			list.add(parser.parse(object.isArray().get(index)));
		}

		return list;
	}

	@Override
	public void write(final StringBuilder builder, final Iterable<T> object)
	{
		if (object == null || !object.iterator().hasNext()) { return; }
		builder.append("[");

		boolean comma = false;
		for (final T item : object)
		{
			if (comma)
			{
				builder.append(",");
			}
			else
			{
				comma = true;
			}

			parser.write(builder, item);
		}

		builder.append("]");
	}
}