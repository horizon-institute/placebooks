package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookInteractionHandler;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class RemoveMapMenuItem extends MenuItem
{
	private final PlaceBookInteractionHandler handler;
	private final PlaceBookItemFrame item;

	public RemoveMapMenuItem(final PlaceBookInteractionHandler handler, final PlaceBookItemFrame item)
	{
		super("Remove from Map");
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
		item.getItem().removeMetadata("mapItemID");
		item.getItemWidget().refresh();
		handler.refreshMap();
		handler.getContext().markChanged();
	}
}
