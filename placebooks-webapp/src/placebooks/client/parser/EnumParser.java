package placebooks.client.parser;

import com.google.gwt.core.client.JsonUtils;

public abstract class EnumParser<T extends Enum<?>> implements JSONParser<Enum<?>>
{
	@Override
	public void write(final StringBuilder builder, final Enum<?> object)
	{
		builder.append(JsonUtils.escapeValue(object.toString()));
	}
}