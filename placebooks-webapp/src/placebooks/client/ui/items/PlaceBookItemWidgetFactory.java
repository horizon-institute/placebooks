package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;

public class PlaceBookItemWidgetFactory
{
	public static PlaceBookItemWidget createItemWidget(final PlaceBookItem item, final boolean editable)
	{
		if (item.is(ItemType.TEXT))
		{
			if(editable)
			{
				return new EditableTextItem(item);
			}
			return new TextItem(item);
		}
		else if (item.is(ItemType.IMAGE))
		{
			return new ImageItem(item);
		}
		else if (item.is(ItemType.AUDIO))
		{
			return new AudioItem(item);
		}
		else if (item.is(ItemType.VIDEO))
		{
			return new VideoItem(item);
		}
		else if (item.is(ItemType.GPS))
		{
			return new MapItem(item);
		}
		else if (item.is(ItemType.WEB))
		{
			return new WebBundleItem(item);
		}
		return null;
	}
}
