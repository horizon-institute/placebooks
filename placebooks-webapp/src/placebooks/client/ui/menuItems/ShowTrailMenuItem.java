package placebooks.client.ui.menuItems;

import placebooks.client.ui.UIMessages;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;

public class ShowTrailMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final DragController controller;
	private final PlaceBookItemFrame item;

	public ShowTrailMenuItem(final DragController controller, final PlaceBookItemFrame item)
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
