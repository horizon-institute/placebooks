package placebooks.client.ui;

import placebooks.client.ui.widget.MenuItem;

public class DeletePlaceBookMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final PlaceBookItemFrame item;

	public DeletePlaceBookMenuItem(final String title, final PlaceBookCanvas canvas, final PlaceBookItemFrame item)
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
