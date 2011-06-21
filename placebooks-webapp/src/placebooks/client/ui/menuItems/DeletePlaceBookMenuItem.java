package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.widget.MenuItem;

public class DeletePlaceBookMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final PlaceBookItemWidget item;

	public DeletePlaceBookMenuItem(final String title, final PlaceBookCanvas canvas, final PlaceBookItemWidget item)
	{
		super(title);
		this.canvas = canvas;
		this.item = item;
	}

	@Override
	public void run()
	{
		canvas.remove(item);
	}
}
