package placebooks.client.ui.menuItems;

import com.google.gwt.core.client.GWT;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class HideTrailMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public HideTrailMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
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
