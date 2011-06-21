package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.openlayers.Event;
import placebooks.client.ui.openlayers.EventHandler;
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
	private PlaceBookCanvas canvas;
	
	public PlaceBookItemWidgetFactory()
	{
	}
	
	protected void setCanvas(PlaceBookCanvas canvas)
	{
		this.canvas = canvas;
	}
	
	protected Widget createWidget(final PlaceBookItemWidget itemWidget)
	{
		PlaceBookItem item = itemWidget.getItem();
		if (item.is(ItemType.TEXT))
		{
			final SimplePanel panel = new SimplePanel();
			panel.getElement().setInnerHTML(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			return panel;
		}
		else if (item.is(ItemType.IMAGE))
		{
			final Image image = new Image();
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
			
			return image;
		}
		else if (item.is(ItemType.AUDIO))
		{
			final Audio audio = Audio.createIfSupported();
			audio.setControls(true);
			
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
			// TODO Handle null key
			final MapWidget panel = new MapWidget(item.getKey());//, canvas);
			panel.addLoadHandler(new EventHandler()
			{
				@Override
				protected void handleEvent(Event event)
				{
					panel.refreshMarkers(canvas.getItems());
				}
			});
			//panel.setWidth("100%");
			//panel.setHeight("100%");
					
			return panel;
		}
		else if (item.is(ItemType.WEB))
		{
			final Frame frame = new Frame(item.getSourceURL());
			
			return frame;
		}
		return null;
	}
	
	public PlaceBookItemWidget createItemWidget(PlaceBookItem item)
	{
		PlaceBookItemWidget itemWidget = new PlaceBookItemWidget(item);
		itemWidget.setContentWidget(createWidget(itemWidget));
		itemWidget.refresh();
		return itemWidget;
	}
}