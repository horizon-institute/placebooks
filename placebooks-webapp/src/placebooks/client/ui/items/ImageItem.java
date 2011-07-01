package placebooks.client.ui.items;

import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

import placebooks.client.model.PlaceBookItem;

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
				fireResizeHandler();	
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
