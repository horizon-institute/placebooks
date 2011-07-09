package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class RemoveMapMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public RemoveMapMenuItem(final SaveContext context, final PlaceBookItemFrame item)
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
		item.getItemWidget().refresh();
		context.markChanged();
	}
}
