package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.views.CompositeView;

public abstract class PlaceBookItemView extends CompositeView<PlaceBookItem>
{
	public static interface FocusHandler
	{
		public void itemFocusChanged(final boolean focussed);
	}

	public static interface ResizeHandler
	{
		public void itemResized();
	}

	public static final double HEIGHT_PRECISION = 10000;

	private final PlaceBookItemController controller;

	private FocusHandler focusHandler;
	private ResizeHandler resizeHandler;

	protected PlaceBookItemView(final PlaceBookItemController controller)
	{
		this.controller = controller;
	}

	public PlaceBookItemController getController()
	{
		return controller;
	}

	public PlaceBookItem getItem()
	{
		return controller.getItem();
	}

	@Override
	public void itemChanged(final PlaceBookItem newItem)
	{
		//Log.info("Item " + newItem.getKey() + " updated");
		refresh();
	}

	public abstract void refresh();

	public String resize()
	{
		return null;
	}

	public void setFocusHandler(final FocusHandler focusHandler)
	{
		this.focusHandler = focusHandler;
	}

	public void setResizeHandler(final ResizeHandler resizeHandler)
	{
		this.resizeHandler = resizeHandler;
	}

	int getOrder()
	{
		return getItem().getParameter("order", 0);
	}

	protected void fireChanged()
	{
		controller.markChanged();
	}

	protected void fireFocusChanged(final boolean focussed)
	{
		if (focusHandler != null)
		{
			focusHandler.itemFocusChanged(focussed);
		}
	}

	protected void fireResized()
	{
		if (resizeHandler != null)
		{
			resizeHandler.itemResized();
		}
	}
}