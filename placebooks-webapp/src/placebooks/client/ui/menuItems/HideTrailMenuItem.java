package placebooks.client.ui.menuItems;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.views.DragController;

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
		return item.getItem().getClassName().equals("placebooks.model.GPSTraceItem")
				&& item.getItem().getMetadata("routeVisible", "true").equals("true");
	}

	@Override
	public void run()
	{
		item.getItem().setMetadata("routeVisible", "false");
		item.getItemWidget().refresh();
		controller.markChanged();
	}
}
