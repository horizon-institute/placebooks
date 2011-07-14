package placebooks.client.ui.items;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

public class ImageItem extends PlaceBookItemWidget
{
	private final Image image = new Image();
	
	private final Timer loadTimer = new Timer()
	{
		@Override
		public void run()
		{
			checkSize();			
		}
	};
	
	private void checkSize()
	{
		if(image.getHeight() == 0)
		{
			loadTimer.schedule(1000);			
		}
		else
		{
			loadTimer.cancel();
			fireResized();
		}
	}

	ImageItem(PlaceBookItem item)
	{
		super(item);
		initWidget(image);
		image.addLoadHandler(new LoadHandler()
		{
			@Override
			public void onLoad(LoadEvent event)
			{
				checkSize();
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
