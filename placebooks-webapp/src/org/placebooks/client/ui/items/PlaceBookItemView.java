package org.placebooks.client.ui.items;

import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.model.Item;
import org.wornchaos.client.ui.CompositeView;

public abstract class PlaceBookItemView extends CompositeView<Item>
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

	private final ItemController controller;

	private FocusHandler focusHandler;
	private ResizeHandler resizeHandler;

	protected PlaceBookItemView(final ItemController controller)
	{
		this.controller = controller;
	}

	public ItemController getController()
	{
		return controller;
	}

	public Item getItem()
	{
		return controller.getItem();
	}

	@Override
	public void itemChanged(final Item newItem)
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