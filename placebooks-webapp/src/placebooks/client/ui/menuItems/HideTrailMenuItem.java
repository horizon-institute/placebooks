package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookItemWidgetFrame;
import placebooks.client.ui.widget.MenuItem;

public class HideTrailMenuItem extends MenuItem
{
	private final PlaceBookItemWidgetFrame item;
	
	public HideTrailMenuItem(String title, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		
		this.item = item;
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
		item.markChanged();
		item.refresh();
	}
}
