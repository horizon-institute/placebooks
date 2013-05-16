package placebooks.client.controllers;

import org.wornchaos.client.controller.Controller;
import org.wornchaos.client.controller.DelegateController;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookItem;

public class PlaceBookItemController extends DelegateController<PlaceBookItem>
{
	private PlaceBookItem item;

	private final boolean canEdit;

	public PlaceBookItemController(final PlaceBookItem item, final Controller<?> controller)
	{
		super(controller);
		canEdit = false;
		setItem(item);
	}

	public PlaceBookItemController(final PlaceBookItem item, final Controller<?> controller, final boolean canEdit)
	{
		super(controller);
		this.canEdit = canEdit;
		setItem(item);
	}

	public boolean canEdit()
	{
		return canEdit;
	}

	@Override
	public PlaceBookItem getItem()
	{
		return item;
	}

	public PlaceBookBinder getPlaceBook()
	{
		if (controller instanceof PlaceBookController) { return ((PlaceBookController) controller).getItem(); }
		return null;
	}

	public void gotoPage(final int page)
	{
		// TODO!
	}

	public void gotoPage(final PlaceBook page)
	{
		// TODO!
	}

	public void setItem(final PlaceBookItem item)
	{
		this.item = item;
	}
}
