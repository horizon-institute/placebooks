package placebooks.client.logger;

import com.google.gwt.logging.client.SystemLogHandler;

public class CustomSysLogHandler extends SystemLogHandler
{
	public CustomSysLogHandler()
	{
		super();
		setFormatter(new LogFormatter());
	}
}
