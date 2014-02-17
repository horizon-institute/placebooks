package org.placebooks.client.model;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Cache;
import org.wornchaos.client.server.JSONServer;
import org.wornchaos.client.server.Prefix;
import org.wornchaos.client.server.Request;
import org.wornchaos.client.server.Request.Method;
import org.wornchaos.client.server.RequestParam;

@Prefix("placebooks")
public interface PlaceBookService extends JSONServer
{
	@Request(value = "group/{group}/add", method = Method.POST)
	public void addGroup(@RequestParam("placebook") final String placebook, @RequestParam("group") final String group,
			final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebook/{id}/delete")
	public void deletePlaceBook(@RequestParam("id") final String id, final AsyncCallback<Shelf> callback);

	@Request(value = "placebook/featured")
	public void getFeaturedPlaceBooks(@RequestParam("count") final int count, final AsyncCallback<Shelf> callback);

	@Request(value = "group/{id}")
	public void getGroup(@RequestParam("id") final String id, final AsyncCallback<Shelf> callback);

	@Request(value = "palette")
	public void getPaletteItems(final AsyncCallback<Iterable<Item>> callback);

	@Cache
	@Request(value = "placebook/{id}")
	public void getPlaceBook(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebook/recent")
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

	@Request(value = "user/add", method = Method.POST)
	public void linkAccount(@RequestParam("username") final String username,
			@RequestParam("password") final String password, @RequestParam("service") final String service,
			final AsyncCallback<Shelf> callback);

	@Request(value = "user/login", method = Method.POST)
	public void login(@RequestParam("email") final String email,
			@RequestParam("password") final String password, final AsyncCallback<Shelf> callback);

	@Request(value = "user/logout")
	public void logout(final AsyncCallback<String> callback);

	@Request(value = "placebook/{id}/download")
	public void placebookPackage(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> response);

	@Request(value = "placebook/publish", method = Method.POST)
	public void publishPlaceBook(@RequestParam("placebook") final PlaceBook placebook, final AsyncCallback<PlaceBook> callback);

	@Request(value = "user/create", method = Method.POST)
	public void registerAccount(@RequestParam("name") final String name, @RequestParam("email") final String email,
			@RequestParam("password") final String password, final AsyncCallback<Shelf> callback);

	@Request(value = "group/save", method = Method.POST)
	public void saveGroup(@RequestParam("shelf") final Shelf shelf, final AsyncCallback<Shelf> callback);

	@Cache
	@Request(value = "placebook/save", method = Method.POST)
	public void savePlaceBook(@RequestParam("placebook") final PlaceBook placebook,
			final AsyncCallback<PlaceBook> callback);

	@Request(value = "search")
	public void search(@RequestParam("terms") final String search, final AsyncCallback<Shelf> callback);

	@Request(value = "search/location")
	public void searchLocation(@RequestParam("geometry") final String geometry, final AsyncCallback<Shelf> callback);

	@Request(value = "sync")
	public void sync(@RequestParam("service") final String service);
}