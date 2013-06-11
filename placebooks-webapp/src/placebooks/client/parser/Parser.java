package placebooks.client.parser;

public interface Parser
{
	public <T> T parse(final Class<T> clazz, final String string);

	public String write(final Object object);
}
