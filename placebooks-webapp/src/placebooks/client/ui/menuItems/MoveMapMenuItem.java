package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookInteractionHandler;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class MoveMapMenuItem extends MenuItem
{
	private final PlaceBookInteractionHandler handler;
	private final PlaceBookItemFrame item;

	public MoveMapMenuItem(final PlaceBookInteractionHandler handler, final PlaceBookItemFrame item)
	{
		super("Move on Map");
		this.item = item;
		this.handler = handler;
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
// TODO		handler.refreshMap();
		handler.getContext().markChanged();
	}
}
