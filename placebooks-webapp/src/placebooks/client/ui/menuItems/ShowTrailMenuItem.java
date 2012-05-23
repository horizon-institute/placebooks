package placebooks.client.ui.menuItems;

import com.google.gwt.core.client.GWT;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class ShowTrailMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public ShowTrailMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.showTrail());

		this.item = item;
		this.controller = controller;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().getClassName().equals("placebooks.model.GPSTraceItem")
				&& item.getItem().getMetadata("routeVisible", "true").equals("false");
	}

	@Override
	public void run()
	{
		item.getItem().removeMetadata("routeVisible");
		item.getItemWidget().refresh();
		controller.markChanged();
	}
}
