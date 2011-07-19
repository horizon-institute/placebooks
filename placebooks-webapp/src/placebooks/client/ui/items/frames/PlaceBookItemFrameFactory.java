package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.PlaceBookItemWidgetFactory;

import com.google.gwt.core.client.GWT;

public abstract class PlaceBookItemFrameFactory
{
	public abstract PlaceBookItemFrame createFrame();

	public PlaceBookItemFrame createFrame(final PlaceBookItem item)
	{
		final PlaceBookItemWidget widget = PlaceBookItemWidgetFactory.createItemWidget(item, getEditable());
		if (widget == null)
		{
			GWT.log("No widget for " + item.getKey() + ": type=" + item.getShortClassName());
			return null;
		}

		final PlaceBookItemFrame frame = createFrame();
		frame.setItemWidget(widget);

		return frame;
	}

	public abstract boolean getEditable();
}