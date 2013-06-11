package placebooks.client.ui.items.frames;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.logger.Log;
import placebooks.client.ui.items.PlaceBookItemView;
import placebooks.client.ui.items.PlaceBookItemViewFactory;
import placebooks.client.ui.views.DragController;

import com.google.gwt.core.client.GWT;

public abstract class PlaceBookItemFrameFactory
{
	public abstract PlaceBookItemFrame createFrame(final DragController handler);

	public PlaceBookItemFrame createFrame(final PlaceBookItemController controller, final DragController handler)
	{
		final PlaceBookItemView widget = PlaceBookItemViewFactory.createItemWidget(controller);
		if (widget == null)
		{
			Log.warn("No widget for " + controller.getItem().getKey() + ": type="
					+ controller.getItem().getShortClassName());
			return null;
		}

		final PlaceBookItemFrame frame = createFrame(handler);
		frame.setItemWidget(widget);

		return frame;
	}
}