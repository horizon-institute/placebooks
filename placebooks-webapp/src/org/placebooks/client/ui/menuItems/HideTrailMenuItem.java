package org.placebooks.client.ui.menuItems;

import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;

public class HideTrailMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public HideTrailMenuItem(final DragController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.hideTrail());

		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().getType() == Type.GPSTraceItem
				&& item.getItem().getMetadata("routeVisible", "true").equals("true");
	}

	@Override
	public void run()
	{
		item.getItem().getMetadata().put("routeVisible", "false");
		item.getItemWidget().refresh();
		controller.markChanged();
	}
}
