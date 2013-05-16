package placebooks.client;

import org.wornchaos.client.parser.JavaScriptObjectParser;
import org.wornchaos.client.server.AbstractJSONServer;

import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.ServerInfo;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PlaceBookService extends AbstractJSONServer
{
	public void addGroup(final String placebook, final String group, final AsyncCallback<PlaceBookBinder> callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/addgroup", "placebookID=" + placebook + "&groupID=" + group,
						new JSONCallback<PlaceBookBinder>(callback, new JavaScriptObjectParser<PlaceBookBinder>()));
	}

	public void deletePlaceBook(final String key, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/deletebinder/" + key, callback);
	}

	public void getPaletteItems(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/palette", callback);
	}

	public void getPlaceBook(final String key, final AsyncCallback<PlaceBookBinder> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/placebookbinder/" + key, new JSONCallback<PlaceBookBinder>(callback,
				new JavaScriptObjectParser<PlaceBookBinder>()));
	}

	public void getPlaceBookGroup(final String id, final AsyncCallback<Shelf> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/group/" + id, new JSONCallback<Shelf>(callback,
				new JavaScriptObjectParser<Shelf>()));
	}

	public void getRandomPlaceBooks(final int count, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/randomized/" + count, callback);
	}

	public void getServerInfo(final AsyncCallback<ServerInfo> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/admin/serverinfo", new JSONCallback<ServerInfo>(callback,
				new JavaScriptObjectParser<ServerInfo>()));
	}

	public void getShelf(final AsyncCallback<Shelf> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/shelf", new JSONCallback<Shelf>(callback,
				new JavaScriptObjectParser<Shelf>()));
	}

	public void getUser(final AsyncCallback<User> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/currentUser", new JSONCallback<User>(callback,
				new JavaScriptObjectParser<User>()));
	}

	public void linkAccount(final String username, final String password, final String service,
			final AsyncCallback<Shelf> callback)
	{
		serverRequest(getHostURL() + "placebooks/a/addLoginDetails", "username=" + username + "&password=" + password
				+ "&service=" + service, new JSONCallback<Shelf>(callback, new JavaScriptObjectParser<Shelf>()));
	}

	public void login(final String email, final String password, final AsyncCallback<Shelf> callback)
	{
		serverRequest(getHostURL() + "j_spring_security_check", "j_username=" + email + "&j_password=" + password
				+ "&_spring_security_remember_me=true", new JSONCallback<Shelf>(callback,
				new JavaScriptObjectParser<Shelf>()));
	}

	public void logout(final RequestCallback callback)
	{
		serverRequest(getHostURL() + "j_spring_security_logout", callback);
	}

	// public static final native <T extends JavaScriptObject> T parse(Class<T> clazz, String json)
	// /*-{ return eval('(' + json + ')'); }-*/;
	public final <T extends JavaScriptObject> T parse(final Class<T> clazz, final String json)
	{
		return JSONParser.parseStrict(json).isObject().getJavaScriptObject().<T> cast();
	}

	public void publishPlaceBook(final PlaceBookBinder placebook, final RequestCallback callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/publishplacebookbinder",
						"placebookbinder=" + URL.encodePathSegment(new JSONObject(placebook).toString()), callback);
	}

	public void registerAccount(final String name, final String email, final String password,
			final AbstractCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/createUserAccount", "name=" + name + "&email=" + email
				+ "&password=" + password, callback);
	}

	public void savePlaceBook(final PlaceBookBinder placebook, final AsyncCallback<PlaceBookBinder> callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/saveplacebookbinder",
						"placebookbinder=" + URL.encodePathSegment(new JSONObject(placebook).toString()),
						new JSONCallback<PlaceBookBinder>(callback, new JavaScriptObjectParser<PlaceBookBinder>()));
	}

	public void savePlaceBookGroup(final Shelf shelf, final AsyncCallback<Shelf> callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/savegroup",
						"group=" + URL.encodePathSegment(new JSONObject(shelf).toString()), new JSONCallback<Shelf>(
								callback, new JavaScriptObjectParser<Shelf>()));
	}

	public void search(final String search, final AsyncCallback<Shelf> callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/admin/search", "terms=" + URL.encodeQueryString(search),
						new JSONCallback<Shelf>(callback, new JavaScriptObjectParser<Shelf>()));
	}

	public void searchLocation(final String geometry, final AsyncCallback<Shelf> callback)
	{
		serverRequest(	getHostURL() + "placebooks/a/admin/location_search/placebookbinder/" + geometry,
						new JSONCallback<Shelf>(callback, new JavaScriptObjectParser<Shelf>()));
	}

	public void sync(final String service, final RequestCallback callback)
	{
		serverRequest(getHostURL() + "placebooks/a/sync/" + service, callback);
	}
}