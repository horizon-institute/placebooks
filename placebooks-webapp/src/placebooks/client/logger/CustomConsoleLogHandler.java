package placebooks.client.logger;

import com.google.gwt.logging.client.ConsoleLogHandler;

public class CustomConsoleLogHandler extends ConsoleLogHandler
{
	public CustomConsoleLogHandler()
	{
		super();
		setFormatter(new LogFormatter());
	}
}
