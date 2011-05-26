package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.widget.MenuItem;

public class AddMapMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final PlaceBookItemFrame item;

	public AddMapMenuItem(final String title, final PlaceBookCanvas canvas, final PlaceBookItemFrame item)
	{
		super(title);
		this.canvas = canvas;
		this.item = item;
	}

	@Override
	public void run()
	{
		final Collection<PlaceBookItem> mapItems = new ArrayList<PlaceBookItem>();
		for(PlaceBookItemFrame itemFrame: canvas.getItems())
		{
			if(itemFrame.getItem().getClassName().equals("placebook.model.GPSTraceItem"))
			{
				mapItems.add(itemFrame.getItem());
			}
		}

		if(mapItems.size() == 1)
		{
			item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
		}
	}
}
