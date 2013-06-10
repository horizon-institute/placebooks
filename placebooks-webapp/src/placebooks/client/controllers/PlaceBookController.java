package placebooks.client.controllers;

import placebooks.client.PlaceBooks;
import placebooks.client.logger.Log;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.parser.JavaScriptObjectParser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class PlaceBookController extends AbstractController<PlaceBookBinder>
{
	private static final String newPlaceBook = "{\"pages\":[{\"items\":[], \"metadata\":{} },{\"items\":[], \"metadata\":{} }]}";

	public PlaceBookController()
	{
		super(new JavaScriptObjectParser<PlaceBookBinder>(), "placebook");
	}

	@Override
	protected boolean accept(final PlaceBookBinder newItem)
	{
		final PlaceBookBinder current = getItem();
		if (current != null)
		{
			final int currentVersion = current.getParameter("version", 1);
			final int newVersion = newItem.getParameter("version", 1);

			// Log.info("Current: " + currentVersion + ", new: " + newVersion);

			if (currentVersion > newVersion)
			{
				markChanged();
				Log.info("Refused new placebook");
				return false;
			}
		}
		return true;
	}

	@Override
	protected String getId()
	{
		if (getItem() != null) { return getItem().getId(); }
		return super.getId();
	}

	@Override
	protected String getKey(final String id)
	{
		if (id == null) { return null; }
		return super.getKey(id);
	}

	@Override
	protected void load(final String id, final AsyncCallback<PlaceBookBinder> callback)
	{
		if (id == null || id.equals("new"))
		{
			callback.onSuccess(PlaceBooks.getServer().parse(PlaceBookBinder.class, newPlaceBook));
			return;
		}
		PlaceBooks.getServer().getPlaceBook(id, callback);
	}

	@Override
	protected void save(final PlaceBookBinder shelf, final AsyncCallback<PlaceBookBinder> callback)
	{
		PlaceBooks.getServer().savePlaceBook(shelf, callback);
	}

	// @Override
	// protected void delete()
	// {
	// PlaceBooks.getServer().deletePlaceBook(placebook.getId(), new AbstractCallback()
	// {
	// @Override
	// public void failure(final Request request, final Response response)
	// {
	// dialog.hide();
	// }
	//
	// @Override
	// public void success(final Request request, final Response response)
	// {
	// dialog.hide();
	// PlaceBooks.goTo(new Home());
	// }
	// });
	// }
}