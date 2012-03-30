package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.PlaceBookItemWidgetFactory;

import com.google.gwt.core.client.GWT;

public abstract class PlaceBookItemFrameFactory
{
	public abstract PlaceBookItemFrame createFrame(final PlaceBookController handler);

	public PlaceBookItemFrame createFrame(final PlaceBookItem item, final PlaceBookController handler)
	{
		final PlaceBookItemWidget widget = PlaceBookItemWidgetFactory.createItemWidget(item, handler);
		if (widget == null)
		{
			GWT.log("No widget for " + item.getKey() + ": type=" + item.getShortClassName());
			return null;
		}

		final PlaceBookItemFrame frame = createFrame(handler);
		frame.setItemWidget(widget);

		return frame;
	}
}