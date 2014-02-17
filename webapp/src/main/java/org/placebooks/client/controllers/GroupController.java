package org.placebooks.client.controllers;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Group;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.model.Shelf;
import org.wornchaos.client.controllers.AbstractController;
import org.wornchaos.client.server.AsyncCallback;

public class GroupController extends AbstractController<Shelf>
{
	@Override
	public void load(final String id)
	{
		if (id.equals("new"))
		{
			final Item image = new Item();
			image.setType(Type.ImageItem);

			final Group newGroup = new Group();
			newGroup.setTitle("Group Title");
			newGroup.setDescription("Group Description");
			newGroup.setImage(image);

			final Shelf shelf = new Shelf();
			shelf.setGroup(newGroup);
			getCallback().onSuccess(shelf);
		}
		else
		{
			PlaceBooks.getServer().getGroup(id, getCallback());
		}
	}

	@Override
	public void refresh()
	{
		if (getItem() != null)
		{
			load(getItem().getGroup().getId());
		}
	}

	@Override
	protected void save(final Shelf shelf, final AsyncCallback<Shelf> callback)
	{
		PlaceBooks.getServer().saveGroup(shelf, callback);
	}
}
