package placebooks.client;

import placebooks.client.model.PlaceBookBinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class PlaceBookService
{
	public static void deletePlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/delete_placebook/" + key, callback);
	}

	public static void getCurrentUser(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/currentUser", callback);
	}

	public static String getHostURL()
	{
		if (GWT.getModuleBaseURL().endsWith(GWT.getModuleName() + "/")) { return GWT.getModuleBaseURL()
				.substring(0, (GWT.getModuleBaseURL().length() - GWT.getModuleName().length() - 1)); }

		return GWT.getModuleBaseURL();
	}

	public static void getPaletteItems(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/palette", callback);
	}

	public static void getPlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebookbinder/" + key, callback);
	}

	public static void getPlaceBookItem(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebookitem/" + key, callback);
	}

	public static void getRandomPlaceBooks(final int count, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/randomized/" + count, callback);
	}

	public static void getServerInfo(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/serverinfo", callback);
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
		serverRequest(getHostURL() + "j_spring_security_check", "j_username=" + email + "&j_password=" + password
				+ "&_spring_security_remember_me=true", callback);
	}

	public static void logout(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "j_spring_security_logout", callback);
	}

	// public static final native <T extends JavaScriptObject> T parse(Class<T> clazz, String json)
	// /*-{ return eval('(' + json + ')'); }-*/;
	public static final <T extends JavaScriptObject> T parse(final Class<T> clazz, final String json)
	{
		return JSONParser.parseStrict(json).isObject().getJavaScriptObject().<T>cast();
	}

	public static void publishPlaceBook(final PlaceBookBinder placebook, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/publishplacebookbinder",
						"placebookbinder=" + URL.encodePathSegment(new JSONObject(placebook).toString()), callback);
	}

	public static void registerAccount(final String name, final String email, final String password,
			final AbstractCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/createUserAccount", "name=" + name + "&email=" + email
				+ "&password=" + password, callback);
	}

	public static void savePlaceBook(final PlaceBookBinder placebook, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/saveplacebookbinder",
						"placebookbinder=" + URL.encodePathSegment(new JSONObject(placebook).toString()), callback);
	}

	public static void search(final String search, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/search", "terms=" + URL.encodeQueryString(search), callback);
	}

	public static void searchLocation(final String geometry, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/location_search/placebookbinder/" + geometry, callback);
	}

	public static void sync(final String service, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/sync/" + service, callback);
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
