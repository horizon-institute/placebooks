package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookSaveItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class ShowTrailMenuItem extends MenuItem
{
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public ShowTrailMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super("Show Trail");

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
