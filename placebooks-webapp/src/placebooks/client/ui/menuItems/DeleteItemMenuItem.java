package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookSaveItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class DeleteItemMenuItem extends MenuItem
{
	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public DeleteItemMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super("Delete");
		this.item = item;
		this.controller = controller;
	}

	@Override
	public void run()
	{
		final PlaceBookColumn panel = item.getPanel();
		panel.getPage().remove(item);
		item.setPanel(null);
		if (panel != null)
		{
			panel.reflow();
		}
		controller.markChanged();
	}
}
