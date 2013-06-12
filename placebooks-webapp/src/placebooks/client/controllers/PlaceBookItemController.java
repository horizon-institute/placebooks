package placebooks.client.controllers;

import placebooks.client.logger.Log;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookItem;

public class PlaceBookItemController extends DelegateController<PlaceBookItem>
{
	private final boolean canEdit;

	public PlaceBookItemController(final PlaceBookItem item, final SimpleController<?> controller)
	{
		super(controller);
		canEdit = false;
		setItem(item);
	}

	public PlaceBookItemController(final PlaceBookItem item, final SimpleController<?> controller, final boolean canEdit)
	{
		super(controller);
		this.canEdit = canEdit;
		setItem(item);
	}

	public boolean canEdit()
	{
		return canEdit;
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
	
	public void removeParameter(final String name)
	{
		if(getItem().hasParameter(name))
		{
			Log.info("Removed " + name + " parameter");
			getItem().removeParameter(name);
			markChanged();
		}
	}
	
	public void setParameter(final String name, final int value)
	{
		if(getItem().hasParameter(name))
		{
			int oldValue = getItem().getParameter(name);
			if(oldValue != value)
			{
				Log.info("Set " + name + " parameter to " + value);				
				markChanged();
			}
		}
		else
		{
			Log.info("Set " + name + " parameter to " + value);			
			markChanged();			
		}
		getItem().setParameter(name, value);
	}

	public void gotoPage(final PlaceBook page)
	{
		// TODO!
	}

	public void setItem(final PlaceBookItem item)
	{
		if(item.getKey() != null)
		{
			item.removeMetadata("tempID");
		}
		
		super.setItem(item);		
	}
}
