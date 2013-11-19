package org.placebooks.client.ui.items;

import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.model.Item;
import org.wornchaos.logger.Log;

public class PlaceBookItemViewFactory
{
	public static PlaceBookItemView createItemWidget(final ItemController controller)
	{
		final Item item = controller.getItem();
		Log.info(item.getType().name());
		switch(item.getType())
		{
			case TextItem:
				if (controller.canEdit()) { return new EditableTextItem(controller); }
				return new TextItem(controller);
			case ImageItem:
				return new ImageItem(controller);
			case AudioItem:
				return new AudioItem(controller);
			case VideoItem:
				return new VideoItem(controller);
			case GPSTraceItem:
				return new MapItem(controller);
			case WebBundleItem:
				return new WebBundleItem(controller);
			default:
				return null;
		}
	}
}
