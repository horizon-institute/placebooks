package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.user.client.ui.Composite;

public abstract class PlaceBookItemWidget extends Composite
{
	public static interface ResizeHandler
	{
		public void itemResized();
	}
	
	public static final double HEIGHT_PRECISION = 10000;

	protected PlaceBookItem item;
	
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
		}
		
		if(newItem.getHash() != null)
		{
			item.setHash(newItem.getHash());
		}
	}

	protected void fireResizeHandler()
	{
		if(resizeHandler != null)
		{
			resizeHandler.itemResized();
		}
	}
}