package placebooks.client.ui;

import placebooks.client.ui.widget.MenuItem;

public class RemoveMapMenuItem extends MenuItem
{
	private final PlaceBookItemWidgetFrame item;

	public RemoveMapMenuItem(final String title, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		this.item = item;
	}

	@Override
	public void run()
	{
		item.getItem().removeMetadata("mapItemID");
		item.refresh();
		item.markChanged();		
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().hasMetadata("mapItemID");
	}
}
