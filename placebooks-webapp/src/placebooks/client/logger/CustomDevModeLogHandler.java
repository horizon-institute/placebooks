package placebooks.client.logger;

import com.google.gwt.logging.client.DevelopmentModeLogHandler;

public class CustomDevModeLogHandler extends DevelopmentModeLogHandler
{
	public CustomDevModeLogHandler()
	{
		super();
		setFormatter(new LogFormatter());
	}
}
