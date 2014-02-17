package org.placebooks.client.ui.items.frames;

import org.placebooks.client.controllers.ItemController;
import org.placebooks.client.ui.items.PlaceBookItemView;
import org.placebooks.client.ui.items.PlaceBookItemViewFactory;
import org.placebooks.client.ui.views.DragController;
import org.wornchaos.logger.Log;

public abstract class PlaceBookItemFrameFactory
{
	public abstract PlaceBookItemFrame createFrame(final DragController handler);

	public PlaceBookItemFrame createFrame(final ItemController controller, final DragController handler)
	{
		final PlaceBookItemView widget = PlaceBookItemViewFactory.createItemWidget(controller);
		if (widget == null)
		{
			Log.warn("No widget for " + controller.getItem().getId() + ": type="
					+ controller.getItem().getType());
			return null;
		}
		controller.add(widget);
		
		final PlaceBookItemFrame frame = createFrame(handler);
		frame.setItemWidget(widget);

		return frame;
	}
}