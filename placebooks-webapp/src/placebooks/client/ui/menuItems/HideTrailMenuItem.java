package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.widget.MenuItem;

public class HideTrailMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemWidget item;

	public HideTrailMenuItem(final SaveContext context, final PlaceBookItemWidget item)
	{
		super("Hide Trail");

		this.item = item;
		this.context = context;
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
		item.refresh();
		context.markChanged();
	}
}
