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

	private ChangeHandler changeHandler;
	private FocusHandler focusHandler;
	private ResizeHandler resizeHandler;

	PlaceBookItemWidget(final PlaceBookItem item)
	{
		this.item = item;
	}

	public PlaceBookItem getItem()
	{
		return item;
	}

	public abstract void refresh();

	public void setChangeHandler(final ChangeHandler changeHandler)
	{
		this.changeHandler = changeHandler;
	}

	public void setFocusHandler(final FocusHandler focusHandler)
	{
		this.focusHandler = focusHandler;
	}

	public void setPlaceBook(final PlaceBook placebook)
	{

	}

	public void setResizeHandler(final ResizeHandler resizeHandler)
	{
		this.resizeHandler = resizeHandler;
	}

	public void update(final PlaceBookItem newItem)
	{
		if (item.getKey() == null && newItem.getKey() != null)
		{
			item.setKey(newItem.getKey());
			item.removeMetadata("tempID");
			newItem.removeMetadata("tempID");
		}
		
		if (newItem.getHash() != null)
		{
			item.setHash(newItem.getHash());
			item.setSourceURL(newItem.getSourceURL());			
		}
		refresh();
	}

	int getOrder()
	{
		return item.getParameter("order", 0);
	}

	protected void fireChanged()
	{
		if (changeHandler != null)
		{
			changeHandler.itemChanged();
		}
	}

	protected void fireFocusChanged(final boolean focussed)
	{
		if (focusHandler != null)
		{
			focusHandler.itemFocusChanged(focussed);
		}
	}
	
	public String resize()
	{
		return null;
	}

	protected void fireResized()
	{
		if (resizeHandler != null)
		{
			resizeHandler.itemResized();
		}
	}
}