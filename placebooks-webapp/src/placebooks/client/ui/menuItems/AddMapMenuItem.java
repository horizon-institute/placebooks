package placebooks.client.ui.menuItems;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.widget.MenuItem;

public class AddMapMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public AddMapMenuItem(final SaveContext context, final PlaceBookCanvas canvas, final PlaceBookItemFrame item)
	{
		super("Add to Map");
		this.canvas = canvas;
		this.item = item;
		this.context = context;
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
		for (final PlaceBookItemFrame itemFrame : canvas.getItems())
		{
			if (itemFrame.getItem().getClassName().equals("placebooks.model.GPSTraceItem"))
			{
				mapItems.add(itemFrame.getItem());
			}
		}

		if (mapItems.size() == 1)
		{
			item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
			context.markChanged();
		}
	}
}
