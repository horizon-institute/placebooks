package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class FitToContentMenuItem extends MenuItem
{
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public FitToContentMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super("Fit to Content");
		this.item = item;
		this.controller = controller;
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
		controller.markChanged();
	}
}
