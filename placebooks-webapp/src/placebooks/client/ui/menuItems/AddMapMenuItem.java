package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookInteractionHandler;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class AddMapMenuItem extends MenuItem
{
	private final PlaceBookInteractionHandler handler;
	private final PlaceBookItemFrame item;

	public AddMapMenuItem(final PlaceBookInteractionHandler handler, final PlaceBookItemFrame item)
	{
		super("Add to Map");
		this.item = item;
		this.handler = handler;
	}

	@Override
	public boolean isEnabled()
	{
		return !item.getItem().getClassName().equals("placebooks.model.GPSTraceItem")
				&& !item.getItem().hasMetadata("mapItemID");
	}

	@Override
	public void run()
	{
		final Collection<PlaceBookItem> mapItems = new ArrayList<PlaceBookItem>();
		for (final PlaceBookItemFrame itemFrame : handler.getCanvas().getItems())
		{
			if (itemFrame.getItem().getClassName().equals("placebooks.model.GPSTraceItem"))
			{
				mapItems.add(itemFrame.getItem());
			}
		}

		if (mapItems.size() == 1)
		{
			item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
			handler.refreshMap();
			handler.getContext().markChanged();
		}
	}
}
