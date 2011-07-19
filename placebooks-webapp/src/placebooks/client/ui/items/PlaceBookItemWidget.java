package placebooks.client.ui.items;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.user.client.ui.Composite;

public abstract class PlaceBookItemWidget extends Composite
{
	public static interface ResizeHandler
	{
		public void itemResized();
	}
	
	public static interface FocusHandler
	{
		public void itemFocusChanged(final boolean focussed);
	}
	
	public static interface ChangeHandler
	{
		public void itemChanged();
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

	public PlaceBookItem getItem()
	{
		return item;
	}

	public void setFocusHandler(FocusHandler focusHandler)
	{
		this.focusHandler = focusHandler;
	}
	
	public void setChangeHandler(ChangeHandler changeHandler)
	{
		this.changeHandler = changeHandler;
	}
		
	public abstract void refresh();

	int getOrder()
	{
		return item.getParameter("order", 0);
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
	
	public void setPlaceBook(final PlaceBook placebook)
	{
		
	}

	protected void fireResized()
	{
		if(resizeHandler != null)
		{
			resizeHandler.itemResized();
		}
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
}