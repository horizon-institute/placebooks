package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.widget.MenuItem;

public class RemoveMapMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemWidget item;

	public RemoveMapMenuItem(final SaveContext context, final PlaceBookItemWidget item)
	{
		super("Remove from Map");
		this.item = item;
		this.context = context;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().hasMetadata("mapItemID");
	}

	@Override
	public void run()
	{
		item.getItem().removeMetadata("mapItemID");
		item.refresh();
		context.markChanged();
	}
}
