package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

public class ImageItem extends PlaceBookItemWidget
{
	private final Image image = new Image();

	ImageItem(PlaceBookItem item)
	{
		super(item);
		initWidget(image);
		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				GWT.log("Image loaded: " + image.getWidth() + ", " + image.getHeight());
				GWT.log("              " + image.getElement().getPropertyInt("naturalWidth") + ", " + image.getElement().getPropertyInt("naturalHeight"));
				GWT.log("              " + image.getElement().getClientWidth() + ", " + image.getElement().getClientHeight());
				GWT.log("              " + image.getElement().getOffsetWidth() + ", " + image.getElement().getOffsetHeight());				
				if(image.getHeight() == 0)
				{
					GWT.log("Size unknown!");
				}
				fireResized();
			}
		});

		image.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				fireFocusChanged(true);
				event.stopPropagation();
			}
		});
	}

	@Override
	public void refresh()
	{
		if (getItem().hasParameter("height"))
		{
			image.setWidth("auto");
		}
		else
		{
			image.setWidth("100%");
		}
		image.setUrl(getItem().getURL());
	}
}
