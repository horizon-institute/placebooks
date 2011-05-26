package placebooks.client.ui;

import placebooks.client.ui.widget.MenuItem;

public class RemoveMapMenuItem extends MenuItem
{
	private final PlaceBookItemFrame item;

	public RemoveMapMenuItem(final String title, final PlaceBookItemFrame item)
	{
		super(title);
		this.item = item;
	}

	@Override
	public void run()
	{
		item.getItem().removeMetadata("mapItemID");
		// TODO Update map
	}
}
