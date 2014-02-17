package org.placebooks.client.ui.menuItems;

import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;

public class FitToContentMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public FitToContentMenuItem(final DragController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.fitToContent());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().getParameters().containsKey("height") && !item.getItem().is(Type.GPSTraceItem);
	}

	@Override
	public void run()
	{
		item.getItem().getParameters().remove("height");
		item.getItemWidget().refresh();
		item.getColumn().reflow();
		controller.markChanged();
	}
}
