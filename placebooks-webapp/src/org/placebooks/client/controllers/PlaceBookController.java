package org.placebooks.client.controllers;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Page;
import org.placebooks.client.model.PlaceBook;
import org.wornchaos.client.controllers.AbstractController;
import org.wornchaos.client.server.AsyncCallback;
import org.wornchaos.logger.Log;

public class PlaceBookController extends AbstractController<PlaceBook>
{
	@Override
	protected boolean accept(final PlaceBook newItem)
	{
		final PlaceBook current = getItem();
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

	public AsyncCallback<PlaceBook> getCallback()
	{
		return super.getCallback();
	}
	
	@Override
	protected void save(final PlaceBook shelf, final AsyncCallback<PlaceBook> callback)
	{
		PlaceBooks.getServer().savePlaceBook(shelf, callback);
	}

	@Override
	public void load(String id)
	{
		if (id == null || id.equals("new"))
		{
			PlaceBook placebook = new PlaceBook();
			placebook.getPages().add(new Page());
			getCallback().onSuccess(placebook);
		}
		else
		{
			PlaceBooks.getServer().getPlaceBook(id, getCallback());
		}
	}

	@Override
	public void refresh()
	{
		if(getItem() != null)
		{
			load(getItem().getId());
		}		
	}
}