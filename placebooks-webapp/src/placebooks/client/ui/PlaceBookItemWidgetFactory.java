package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.openlayers.MapWidget;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemWidgetFactory
{
	public PlaceBookItemWidgetFactory()
	{
	}

	public final PlaceBookItemWidget createPlaceBookItemWidget(final PlaceBookCanvas canvas, final PlaceBookItem item)
	{
		final PlaceBookItemWidget itemWidget = createItemWidget(canvas, item);
		final Widget widget = createWidget(item);
		setupEventHandlers(itemWidget, widget);
		itemWidget.setContentWidget(widget);
		itemWidget.refresh();
		return itemWidget;
	}

	protected PlaceBookItemWidget createItemWidget(final PlaceBookCanvas canvas, final PlaceBookItem item)
	{
		return new PlaceBookItemWidget(canvas, item);
	}

	protected Widget createWidget(final PlaceBookItem item)
	{
		if (item.is(ItemType.TEXT))
		{
			final SimplePanel panel = new SimplePanel();
			panel.getElement().setInnerHTML(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			return panel;
		}
		else if (item.is(ItemType.IMAGE))
		{
			return new Image();
		}
		else if (item.is(ItemType.AUDIO))
		{
			final Audio audio = Audio.createIfSupported();
			audio.setControls(true);
			audio.setWidth("100%");
			return audio;
		}
		else if (item.is(ItemType.VIDEO))
		{
			final Video video = Video.createIfSupported();
			video.setControls(true);
			return video;
		}
		else if (item.is(ItemType.GPS))
		{
			if (!item.hasParameter("height"))
			{
				item.setParameter("height", 5000);
			}

			// TODO Handle null key
			final MapWidget panel = new MapWidget(item.getKey());
			panel.setWidth("100%");

			return panel;
		}
		else if (item.is(ItemType.WEB))
		{
			final Frame frame = new Frame(item.getSourceURL());
			return frame;
		}
		return null;
	}

	protected void setupEventHandlers(final PlaceBookItemWidget itemWidget, final Widget widget)
	{
		final PlaceBookItem item = itemWidget.getItem();
		if (item.is(ItemType.IMAGE))
		{
			final Image image = (Image) widget;
			image.addLoadHandler(new LoadHandler()
			{
				@Override
				public void onLoad(final LoadEvent event)
				{
					if (itemWidget.getPanel() != null)
					{
						itemWidget.getPanel().reflow();
					}
				}
			});
		}
	}
}