package placebooks.client.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

import placebooks.client.parser.Parser;

import com.google.gwt.core.shared.GWT;

public class Log
{
	private static final Logger logger;

	static
	{
		if (GWT.isClient())
		{
			logger = Logger.getLogger("Client");
		}
		else
		{
			logger = Logger.getLogger("Server");
		}
	}

	public static void error(final String message)
	{
		logger.severe(message);
	}

	public static void error(final String message, final Throwable throwable)
	{
		logger.log(Level.SEVERE, message, throwable);
	}

	public static void error(final Throwable throwable)
	{
		logger.log(Level.SEVERE, throwable.getMessage(), throwable);
	}

	public static void info(final Object object, final Parser parser)
	{
		logger.info(parser.write(object));
	}

	public static void info(final String message)
	{
		logger.info(message);
	}

	public static void info(final String message, final Object object, final Parser modelCodec)
	{
		logger.info(message + modelCodec.write(object));
	}

	public static void info(final String message, final Throwable throwable)
	{
		logger.log(Level.INFO, message, throwable);
	}

	public static void warn(final String message)
	{
		logger.warning(message);
	}

	public static void warn(final String message, final Throwable throwable)
	{
		// logger.log(Level.WARNING, message, throwable);
	}
}