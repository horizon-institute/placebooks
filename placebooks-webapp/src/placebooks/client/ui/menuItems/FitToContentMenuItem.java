package placebooks.client.ui.menuItems;

import placebooks.client.ui.PlaceBookEditor.SaveContext;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.widget.MenuItem;

public class FitToContentMenuItem extends MenuItem
{
	private final SaveContext context;
	private final PlaceBookItemFrame item;

	public FitToContentMenuItem(final SaveContext context, final PlaceBookItemFrame item)
	{
		super("Fit to Content");
		this.item = item;
		this.context = context;
	}

	@Override
	public boolean isEnabled()
	{
		return item.getItem().hasParameter("height");
	}

	@Override
	public void run()
	{
		item.getItem().removeParameter("height");
		item.getPanel().reflow();
		context.markChanged();
	}
}
