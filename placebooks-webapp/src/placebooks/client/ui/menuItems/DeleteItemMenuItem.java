package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.elements.PlaceBookCanvas;
import placebooks.client.ui.elements.PlaceBookPanel;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class DeleteItemMenuItem extends MenuItem
{
	private final PlaceBookCanvas canvas;
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public DeleteItemMenuItem(final SaveContext context, final PlaceBookCanvas canvas, final PlaceBookItemFrame item)
	{
		super("Delete");
		this.canvas = canvas;
		this.item = item;
		this.context = context;
	}

	@Override
	public void run()
	{
		final PlaceBookPanel panel = item.getPanel();
		canvas.remove(item);
		if (panel != null)
		{
			panel.reflow();
		}
		context.markChanged();
	}
}
