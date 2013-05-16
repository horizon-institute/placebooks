package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;

public class PlaceBookItemViewFactory
{
	public static PlaceBookItemView createItemWidget(final PlaceBookItemController controller)
	{
		final PlaceBookItem item = controller.getItem();
		if (item.is(ItemType.TEXT))
		{
			if (controller.canEdit()) { return new EditableTextItem(controller); }
			return new TextItem(controller);
		}
		else if (item.is(ItemType.IMAGE))
		{
			return new ImageItem(controller);
		}
		else if (item.is(ItemType.AUDIO))
		{
			return new AudioItem(controller);
		}
		else if (item.is(ItemType.VIDEO))
		{
			return new VideoItem(controller);
		}
		else if (item.is(ItemType.GPS))
		{
			return new MapItem(controller);
		}
		else if (item.is(ItemType.WEB)) { return new WebBundleItem(controller); }
		return null;
	}
}
