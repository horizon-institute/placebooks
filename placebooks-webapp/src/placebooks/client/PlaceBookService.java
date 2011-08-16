package placebooks.client;

import placebooks.client.model.PlaceBook;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;

public class PlaceBookService
{
	public static void everytrail(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/import_everytrail", callback);
	}

	public static void getPaletteItems(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/palette", callback);
	}

	public static void getPlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebook/" + key, callback);
	}

	public static void deletePlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/delete_placebook/" + key, callback);
	}
	
	public static void getPlaceBookItem(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebookitem/" + key, callback);
	}

	public static void getShelf(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/shelf", callback);
	}

	public static void linkAccount(final String username, final String password, final String service,
			final AbstractCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/addLoginDetails", "username=" + username + "&password=" + password
				+ "&service=" + service, callback);
	}

	public static void login(final String email, final String password, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "j_spring_security_check", "j_username=" + email + "&j_password=" + password,
						callback);
	}

	public static void logout(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "j_spring_security_logout", callback);
	}

	public static void publishPlaceBook(final PlaceBook placebook, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/publishplacebook",
						"placebook=" + URL.encodePathSegment(new JSONObject(placebook).toString()), callback);
	}

	public static void registerAccount(final String name, final String email, final String password,
			final AbstractCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/createUserAccount", "name=" + name + "&email=" + email
				+ "&password=" + password, callback);
	}

	public static void savePlaceBook(final PlaceBook placebook, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/saveplacebook",
						"placebook=" + URL.encodePathSegment(new JSONObject(placebook).toString()), callback);
	}

	public static void search(final String search, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/search", "terms=" + URL.encodeQueryString(search), callback);
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
		GWT.log("Request: " + url);
		final RequestBuilder builder = new RequestBuilder(method, URL.encode(url));
		if (data != null)
		{
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			GWT.log("Request data: " + URL.decodePathSegment(data));
			GWT.log("Request data: " + data);			
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

	private static void serverRequest(final String url, final String data, final RequestCallback callback)
	{
		serverRequest(url, RequestBuilder.POST, data, callback);
	}
}
