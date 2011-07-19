package placebooks.client.ui.items;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.user.client.ui.Composite;

public abstract class PlaceBookItemWidget extends Composite
{
	public static interface ChangeHandler
	{
		public void itemChanged();
	}
	
	public static interface FocusHandler
	{
		public void itemFocusChanged(final boolean focussed);
	}
	
	public static interface ResizeHandler
	{
		public void itemResized();
	}	
		
	public static final double HEIGHT_PRECISION = 10000;

	protected PlaceBookItem item;
	
	private ResizeHandler resizeHandler;
	private FocusHandler focusHandler;
	private ChangeHandler changeHandler;	

	PlaceBookItemWidget(final PlaceBookItem item)
	{
		this.item = item;
	}

	protected void fireChanged()
	{
		if(changeHandler != null)
		{
			changeHandler.itemChanged();
		}
	}

	protected void fireFocusChanged(final boolean focussed)
	{
		if(focusHandler != null)
		{
			focusHandler.itemFocusChanged(focussed);
		}
	}
	
	protected void fireResized()
	{
		if(resizeHandler != null)
		{
			resizeHandler.itemResized();
		}
	}
		
	public PlaceBookItem getItem()
	{
		return item;
	}

	int getOrder()
	{
		return item.getParameter("order", 0);
	}
	
	public abstract void refresh();
	
	public void setChangeHandler(ChangeHandler changeHandler)
	{
		this.changeHandler = changeHandler;
	}
	
	public void setFocusHandler(FocusHandler focusHandler)
	{
		this.focusHandler = focusHandler;
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		
	}

	public void setResizeHandler(ResizeHandler resizeHandler)
	{
		this.resizeHandler = resizeHandler;
	}
	
	public void update(PlaceBookItem newItem)
	{
		if(item.getKey() == null && newItem.getKey() != null)
		{
			item.setKey(newItem.getKey());
			item.removeMetadata("tempID");
			newItem.removeMetadata("tempID");
		}
		
		if(newItem.getHash() != null)
		{
			item.setHash(newItem.getHash());
		}
		refresh();
	}	
}