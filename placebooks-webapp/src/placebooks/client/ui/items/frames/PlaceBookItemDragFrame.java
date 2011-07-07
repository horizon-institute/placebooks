package placebooks.client.ui.items.frames;

import placebooks.client.resources.Resources;


public class PlaceBookItemDragFrame extends PlaceBookItemFrameWidget
{
	public PlaceBookItemDragFrame()
	{
		super();
		initWidget(createFrame());
		
		widgetPanel.setStyleName(Resources.INSTANCE.style().frameWidgetPanel());

		frame.add(widgetPanel);
		frame.setStyleName(Resources.INSTANCE.style().dragFrame());
	}
}
