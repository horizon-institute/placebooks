package placebooks.client.ui.menuItems;

import placebooks.client.controllers.PlaceBookController;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;

public class DeleteItemMenuItem extends MenuItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final PlaceBookController controller;
	private final PlaceBookItemFrame item;

	public DeleteItemMenuItem(final PlaceBookController controller, final PlaceBookItemFrame item)
	{
		super(uiMessages.delete());
		this.item = item;
		this.controller = controller;
	}

	@Override
	public void run()
	{
		final PlaceBookColumn panel = item.getColumn();
		panel.getPage().remove(item);
		item.setColumn(null);
		if (panel != null)
		{
			panel.reflow();
		}

		if (item.getItem().is(ItemType.GPS))
		{
			for (final PlaceBook placebook : controller.getItem().getPages())
			{
				for (final PlaceBookItem pbItem : placebook.getItems())
				{
					if (pbItem.getMetadata("mapItemID", "").equals(item.getItem().getKey()))
					{
						pbItem.removeMetadata("mapItemID");
					}
				}
			}
		}

		controller.markChanged();
	}
}
