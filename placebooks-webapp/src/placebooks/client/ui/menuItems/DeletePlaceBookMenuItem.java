package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.PlaceBookPanel;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class DeletePlaceBookMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public DeletePlaceBookMenuItem(final SaveContext context, final PlaceBookCanvas canvas,
			final PlaceBookItemFrame item)
	{
		super("Delete");
		this.canvas = canvas;
		this.item = item;
		this.context = context;
	}

	@Override
	public void run()
	{
		PlaceBookPanel panel = item.getPanel();
		canvas.remove(item);
		if(panel != null)
		{
			panel.reflow();
		}
		context.markChanged();
	}
}
