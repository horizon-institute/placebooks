package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class ShowTrailMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public ShowTrailMenuItem(final SaveContext context, final PlaceBookItemFrame item)
	{
		super("Show Trail");

		this.item = item;
		this.context = context;
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
		context.markChanged();
	}
}
