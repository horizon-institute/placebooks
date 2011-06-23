package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.PlaceBookItemWidgetFrame;
import placebooks.client.ui.widget.MenuItem;

public class AddMapMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final PlaceBookItemWidgetFrame item;

	public AddMapMenuItem(final String title, final PlaceBookCanvas canvas, final PlaceBookItemWidgetFrame item)
	{
		super(title);
		this.canvas = canvas;
		this.item = item;
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
		for (final PlaceBookItemWidget itemFrame : canvas.getItems())
		{
			if (itemFrame.getItem().getClassName().equals("placebooks.model.GPSTraceItem"))
			{
				mapItems.add(itemFrame.getItem());
			}
		}

		if (mapItems.size() == 1)
		{
			item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
			item.markChanged();
		}
	}
}
