package org.placebooks.client.ui.items.frames;

import org.placebooks.client.Resources;

public class PlaceBookItemDragFrame extends PlaceBookItemFrameWidget
{
	public PlaceBookItemDragFrame()
	{
		super();
		rootPanel = createFrame();

		widgetPanel.setStyleName(Resources.STYLES.style().frameWidgetPanel());

		frame.add(widgetPanel);
		frame.setStyleName(Resources.STYLES.style().dragFrame());
	}
}
