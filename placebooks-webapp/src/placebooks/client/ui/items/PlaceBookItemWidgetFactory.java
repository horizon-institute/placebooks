package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.elements.PlaceBookController;

public class PlaceBookItemWidgetFactory
{
	public static PlaceBookItemWidget createItemWidget(final PlaceBookItem item, final PlaceBookController handler)
	{
		if (item.is(ItemType.TEXT))
		{
			if (handler.canEdit()) { return new EditableTextItem(item, handler); }
			return new TextItem(item, handler);
		}
		else if (item.is(ItemType.IMAGE))
		{
			return new ImageItem(item, handler);
		}
		else if (item.is(ItemType.AUDIO))
		{
			return new AudioItem(item, handler);
		}
		else if (item.is(ItemType.VIDEO))
		{
			return new VideoItem(item, handler);
		}
		else if (item.is(ItemType.GPS))
		{
			return new MapItem(item, handler);
		}
		else if (item.is(ItemType.WEB)) { return new WebBundleItem(item, handler); }
		return null;
	}
}
