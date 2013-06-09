package placebooks.client.ui.items;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.ui.dialogs.PlaceBookUploadDialog;

import com.google.gwt.dom.client.Style.Cursor;
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

	public ImageItem(final PlaceBookItemController controller)
	{
		super(controller);
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

		if (controller.canEdit())
		{
			image.getElement().getStyle().setCursor(Cursor.POINTER);
			image.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					fireFocusChanged(true);
					event.stopPropagation();
					final PlaceBookUploadDialog dialog = new PlaceBookUploadDialog(getController(), ImageItem.this);
					dialog.show();
				}
			});
		}

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