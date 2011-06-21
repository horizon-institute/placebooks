package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookItemWidgetFrame;
import placebooks.client.ui.widget.MenuItem;

public class ShowTrailMenuItem extends MenuItem
{
	private final PlaceBookItemWidgetFrame item;
	
	public ShowTrailMenuItem(String title, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		
		this.item = item;
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
		item.markChanged();
		item.refresh();
	}
}
