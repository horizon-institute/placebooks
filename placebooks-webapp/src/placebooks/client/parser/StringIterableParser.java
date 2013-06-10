package placebooks.client.parser;

public class StringIterableParser extends IterableParser<String>
{
	public static final StringIterableParser INSTANCE = new StringIterableParser();

	public StringIterableParser()
	{
		super(StringParser.INSTANCE);
	}
}
