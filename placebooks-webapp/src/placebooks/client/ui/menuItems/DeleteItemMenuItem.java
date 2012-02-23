package placebooks.client.ui.menuItems;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.elements.PlaceBookColumn;
import placebooks.client.ui.elements.PlaceBookController;
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
		final PlaceBookColumn panel = item.getColumn();
		panel.getPage().remove(item);
		item.setPanel(null);
		if (panel != null)
		{
			panel.reflow();
		}
		
		if(item.getItem().is(ItemType.GPS))
		{
			for(PlaceBook placebook: controller.getPages().getPlaceBook().getPages())
			{
				for(PlaceBookItem pbItem: placebook.getItems())
				{
					if(pbItem.getMetadata("mapItemID", "").equals(item.getItem().getKey()))
					{					
						pbItem.removeMetadata("mapItemID");
					}
				}
			}
		}
		
		controller.markChanged();
	}
}
