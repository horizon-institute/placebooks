package placebooks.client.parser;

import com.google.gwt.json.client.JSONValue;

public interface JSONParser<T extends Object>
{
	public T parse(final JSONValue object);

	public void write(final StringBuilder builder, final T object);
}
