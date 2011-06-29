package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.widget.MenuItem;

public class ShowTrailMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemWidget item;

	public ShowTrailMenuItem(final SaveContext context, final PlaceBookItemWidget item)
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
		item.refresh();
		context.markChanged();
	}
}
