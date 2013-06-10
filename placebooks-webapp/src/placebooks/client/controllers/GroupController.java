package placebooks.client.controllers;

import placebooks.client.PlaceBooks;
import placebooks.client.model.Shelf;
import placebooks.client.parser.JavaScriptObjectParser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GroupController extends AbstractController<Shelf>
{
	private static final String newGroup = "{\"entries\":[],\"group\":{\"title\":\"Group Title\",\"description\":\"Group Description\",\"image\":{\"@class\":\"placebooks.model.ImageItem\",\"metadata\":{},\"parameters\":{}}}}";

	public GroupController()
	{
		super(new JavaScriptObjectParser<Shelf>(), "group");
	}

	@Override
	protected String getId()
	{
		if (getItem() != null && getItem().getGroup() != null) { return getItem().getGroup().getId(); }
		return super.getId();
	}

	@Override
	protected String getKey(final String id)
	{
		if (id == null) { return null; }
		return super.getKey(id);
	}

	@Override
	protected void load(final String id, final AsyncCallback<Shelf> callback)
	{
		if (id.equals("new"))
		{
			callback.onSuccess(PlaceBooks.getServer().parse(Shelf.class, newGroup));
			return;
		}
		PlaceBooks.getServer().getPlaceBookGroup(id, callback);
	}

	@Override
	protected void save(final Shelf shelf, final AsyncCallback<Shelf> callback)
	{
		PlaceBooks.getServer().savePlaceBookGroup(shelf, callback);
	}
}
