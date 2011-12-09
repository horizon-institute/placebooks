package placebooks.client.ui.menuItems;

import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.elements.PlaceBookSaveItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

public class DeleteItemMenuItem extends MenuItem
{
	private final PlaceBookSaveItem context;
	private final PlaceBookItemFrame item;

	public DeleteItemMenuItem(final PlaceBookSaveItem context, final PlaceBookItemFrame item)
	{
		super("Delete");
		this.item = item;
		this.context = context;
	}

	@Override
	public void run()
	{
		final PlaceBookColumn panel = item.getPanel();
		panel.getPage().remove(item.getItem());
		item.setPanel(null);
		if (panel != null)
		{
			panel.reflow();
		}
		context.markChanged();
	}
}
