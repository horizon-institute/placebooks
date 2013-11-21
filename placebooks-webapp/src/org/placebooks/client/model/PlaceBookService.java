package org.placebooks.client.model;

import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.client.server.Cache;
import org.wornchaos.client.server.JSONServer;
import org.wornchaos.client.server.Request;
import org.wornchaos.client.server.Request.Method;
import org.wornchaos.client.server.RequestParam;

public interface PlaceBookService extends JSONServer
{
	@Request(value = "placebooks/a/addgroup", method=Method.POST)
	public void addGroup(@RequestParam("placebook") final String placebook, @RequestParam("group") final String group, final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebooks/a/deleteplacebook")
	public void deletePlaceBook(@RequestParam("placebook") final String placebook, final AsyncCallback<Shelf> callback);

	@Request(value = "placebooks/a/palette")
	public void getPaletteItems(final AsyncCallback<Iterable<Item>> callback);

	@Cache
	@Request(value = "placebooks/a/placebook")
	public void getPlaceBook(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebooks/a/group")
	public void getGroup(@RequestParam("id") final String id, final AsyncCallback<Shelf> callback);

	@Request(value = "placebooks/a/featured")
	public void getFeaturedPlaceBooks(@RequestParam("count") final int count, final AsyncCallback<Shelf> callback);

	@Cache
	@Request("placebooks/a/serverinfo")
	public void getServerInfo(final AsyncCallback<ServerInfo> callback);
	
	@Cache("Library")	
	@Request("placebooks/a/shelf")
	public void getShelf(final AsyncCallback<Shelf> callback);

	@Cache("User")	
	@Request("placebooks/a/user")
	public void getUser(final AsyncCallback<User> callback);

	@Request(value = "placebooks/a/addLoginDetails", method=Method.POST)
	public void linkAccount(@RequestParam("username") final String username, @RequestParam("password") final String password, @RequestParam("service") final String service,
			final AsyncCallback<Shelf> callback);

	@Request(value = "j_spring_security_check", method=Method.POST)
	public void login(@RequestParam("j_username") final String email, @RequestParam("j_password") final String password, @RequestParam("_spring_security_remember_me") boolean remember, final AsyncCallback<Shelf> callback);

	@Request(value = "j_spring_security_logout")
	public void logout(final AsyncCallback<String> callback);

	@Request(value = "placebooks/a/publishplacebook", method=Method.POST)
	public void publishPlaceBook(final PlaceBook placebook, final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebooks/a/createaccount", method=Method.POST)
	public void registerAccount(@RequestParam("name") final String name, @RequestParam("email") final String email, @RequestParam("password") final String password,
			final AsyncCallback<Shelf> callback);

	@Cache
	@Request(value= "placebooks/a/saveplacebook", method=Method.POST)
	public void savePlaceBook(@RequestParam("placebook") final PlaceBook placebook, final AsyncCallback<PlaceBook> callback);

	@Request(value = "placebooks/a/savegroup", method=Method.POST)
	public void saveGroup(final Shelf shelf, final AsyncCallback<Shelf> callback);

	@Request(value = "placebooks/a/search")
	public void search(@RequestParam("terms") final String search, final AsyncCallback<Shelf> callback);

	@Request(value = "placebooks/a/searchlocation")
	public void searchLocation(@RequestParam("geometry") final String geometry, final AsyncCallback<Shelf> callback);

	@Request(value = "placebooks/a/sync")
	public void sync(@RequestParam("service") final String service);
	
	@Request(value = "admin/package")
	public void placebookPackage(@RequestParam("id") final String id, final AsyncCallback<PlaceBook> response);	
}