package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.PlaceBookItemWidget;
import placebooks.client.ui.widget.MenuItem;

public class DeletePlaceBookMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final SaveContext context;
	private final PlaceBookItemWidget item;

	public DeletePlaceBookMenuItem(final SaveContext context, final PlaceBookCanvas canvas,
			final PlaceBookItemWidget item)
	{
		super("Delete");
		this.canvas = canvas;
		this.item = item;
		this.context = context;
	}

	@Override
	public void run()
	{
		canvas.remove(item);
		context.markChanged();
	}
}
