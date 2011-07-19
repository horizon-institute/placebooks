package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class MoveMapMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public MoveMapMenuItem(final SaveContext context, final PlaceBookItemFrame item)
	{
		super("Move on Map");
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
		item.getItem().setGeometry(null);
		item.getItemWidget().refresh();
		context.refreshMap();
		context.markChanged();
	}
}
