package placebooks.client.parser;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JavaScriptObjectParser<T extends JavaScriptObject> implements JSONParser<T>
{
	@Override
	public T parse(final JSONValue object)
	{
		if (object == null || object.isObject() == null) { return null; }
		return object.isObject().getJavaScriptObject().cast();
	}

	@Override
	public void write(final StringBuilder builder, final T object)
	{
		builder.append(new JSONObject(object).toString());
	}
}
