package org.placebooks.client.model;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Cache;
import org.wornchaos.client.server.JSONServer;
import org.wornchaos.client.server.Prefix;
import org.wornchaos.client.server.Request;
import org.wornchaos.client.server.Request.Method;
import org.wornchaos.client.server.RequestParam;

@Prefix("command")
public interface PlaceBookService extends JSONServer
{
	@Request(value = "addgroup", method = Method.POST)
	public void addGroup(@RequestParam("placebook") final String placebook, @RequestParam("group") final String group,
			final AsyncCallback<PlaceBook> callback);

	@Request(value = "deleteplacebook")
	public void deletePlaceBook(@RequestParam("placebook") final String placebook, final AsyncCallback<Shelf> callback);

	@Request(value = "featured")
	public void getFeaturedPlaceBooks(@RequestParam("count") final int count, final AsyncCallback<Shelf> callback);

	@Request(value = "group")
	public void getGroup(@RequestParam("id") final String id, final AsyncCallback<Shelf> callback);

	@Request(value = "palette")
	public void getPaletteItems(final AsyncCallback<Iterable<Item>> callback);

	@Cache
	@Request(value = "placebook")
	public void getPlaceBook(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> callback);

	@Request(value = "recent")
	public void getRecentPlaceBooks(AsyncCallback<String> callback);

	@Cache
	@Request("serverinfo")
	public void getServerInfo(final AsyncCallback<ServerInfo> callback);

	@Cache("Library")
	@Request("shelf")
	public void getShelf(final AsyncCallback<Shelf> callback);

	@Cache("User")
	@Request("user")
	public void getUser(final AsyncCallback<User> callback);

	@Request(value = "addLoginDetails", method = Method.POST)
	public void linkAccount(@RequestParam("username") final String username,
			@RequestParam("password") final String password, @RequestParam("service") final String service,
			final AsyncCallback<Shelf> callback);

	@Request(value = "login", method = Method.POST)
	public void login(@RequestParam("email") final String email,
			@RequestParam("password") final String password, final AsyncCallback<Shelf> callback);

	@Request(value = "logout")
	public void logout(final AsyncCallback<String> callback);

	@Request(value = "package")
	public void placebookPackage(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> response);

	@Request(value = "publishplacebook", method = Method.POST)
	public void publishPlaceBook(final PlaceBook placebook, final AsyncCallback<PlaceBook> callback);

	@Request(value = "createaccount", method = Method.POST)
	public void registerAccount(@RequestParam("name") final String name, @RequestParam("email") final String email,
			@RequestParam("password") final String password, final AsyncCallback<Shelf> callback);

	@Request(value = "savegroup", method = Method.POST)
	public void saveGroup(@RequestParam("shelf") final Shelf shelf, final AsyncCallback<Shelf> callback);

	@Cache
	@Request(value = "saveplacebook", method = Method.POST)
	public void savePlaceBook(@RequestParam("placebook") final PlaceBook placebook,
			final AsyncCallback<PlaceBook> callback);

	@Request(value = "search")
	public void search(@RequestParam("terms") final String search, final AsyncCallback<Shelf> callback);

	@Request(value = "searchlocation")
	public void searchLocation(@RequestParam("geometry") final String geometry, final AsyncCallback<Shelf> callback);

	@Request(value = "sync")
	public void sync(@RequestParam("service") final String service);
}