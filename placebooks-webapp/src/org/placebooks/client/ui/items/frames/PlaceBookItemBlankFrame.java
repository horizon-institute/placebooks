package org.placebooks.client.ui.items.frames;

import org.placebooks.client.Resources;
import org.placebooks.client.ui.views.DragController;

public class PlaceBookItemBlankFrame extends PlaceBookItemFrame
{
	public static PlaceBookItemFrameFactory FACTORY = new PlaceBookItemFrameFactory()
	{
		@Override
		public PlaceBookItemFrame createFrame(final DragController handler)
		{
			return new PlaceBookItemBlankFrame();
		}
	};

	public PlaceBookItemBlankFrame()
	{
		rootPanel = widgetPanel;
		widgetPanel.setStyleName(Resources.STYLES.style().widgetPanel());
	}
}
