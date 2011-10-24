package placebooks.client.ui.elements;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DatePrinter
{
	private static final int MILLISECONDSINDAY = 86400000;
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("d MMM");
	private static final DateTimeFormat TIME_FORMAT = DateTimeFormat.getFormat("HH:mm");	
	
	public static String formatDate(final Date date)
	{
		if(isToday(date))
		{
			return TIME_FORMAT.format(date);
		}
		else
		{
			return DATE_FORMAT.format(date);
		}
	}
	
	public static boolean isToday(final Date date)
	{
		Date now = new Date();
		long today = now.getTime() / MILLISECONDSINDAY;
		long day = date.getTime() / MILLISECONDSINDAY;
		return today == day;
	}
}
