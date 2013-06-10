package placebooks.client.logger;

import java.util.Date;
import java.util.logging.LogRecord;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.logging.impl.FormatterImpl;

public class LogFormatter extends FormatterImpl
{
	private final DateTimeFormat dateFormat = DateTimeFormat.getFormat("h:mm:ss a");

	@Override
	public String format(final LogRecord record)
	{
		final StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(dateFormat.format(new Date(record.getMillis())));
		buffer.append("] ");
		buffer.append(record.getLoggerName());
		buffer.append(" ");
		buffer.append(record.getLevel());
		buffer.append(": ");
		buffer.append(record.getMessage());
		if (record.getThrown() != null)
		{
			buffer.append(getStackTraceAsString(record.getThrown(), "\n", "\t"));
		}
		return buffer.toString();
	}
}
