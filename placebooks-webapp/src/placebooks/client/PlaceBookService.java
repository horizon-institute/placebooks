package placebooks.client;

import placebooks.client.model.PlaceBook;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;

public class PlaceBookService
{
	public static void getPlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebook/" + key, callback);
	}

	public static void getShelf(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/shelf", callback);
	}

	public static void savePlaceBook(final PlaceBook placebook, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/saveplacebook", RequestBuilder.POST, "placebook="
				+ new JSONObject(placebook).toString(), callback);
	}
	
	public static void getPaletteItems(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/palette", callback);
	}

	private static String getHostURL()
	{
		final String url = GWT.getHostPageBaseURL();
		// if (url.endsWith("taxishare-ui/")) { return url.substring(0, url.length() -
		// "taxishare-ui/".length())
		// + "taxishare-service/"; }

		return url;
	}

	private static void serverRequest(final String url, final RequestBuilder.Method method, final String data,
			final RequestCallback callback)
	{
		GWT.log(url);
		GWT.log(data);
		final RequestBuilder builder = new RequestBuilder(method, URL.encode(url));
		if (data != null)
		{
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		try
		{
			builder.sendRequest(data, callback);
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
	}

	private static void serverRequest(final String url, final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.GET, null, callback);
	}
}
