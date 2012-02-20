package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ImageItem extends MediaItem
{
	private final Image image = new Image();

	ImageItem(final PlaceBookItem item, final PlaceBookController handler)
	{
		super(item, handler);
		image.getElement().getStyle().setProperty("margin", "0 auto");
		image.getElement().getStyle().setDisplay(Display.BLOCK);

		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(final LoadEvent event)
			{
				checkSize();
			}
		});

		image.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				fireFocusChanged(true);
				event.stopPropagation();
			}
		});

		refresh();
	}

	@Override
	protected void checkHeightParam()
	{
		if (getItem().hasParameter("height"))
		{
			getMediaWidget().setWidth("auto");
			getMediaWidget().setHeight("100%");
		}
		else
		{
			getMediaWidget().setWidth("100%");
			getMediaWidget().setHeight("auto");
		}
	}

	@Override
	protected int getMediaHeight()
	{
		return image.getHeight();
	}

	@Override
	protected Widget getMediaWidget()
	{
		return image;
	}

	@Override
	protected void setURL(final String url)
	{
		image.setUrl(url);
	}
}