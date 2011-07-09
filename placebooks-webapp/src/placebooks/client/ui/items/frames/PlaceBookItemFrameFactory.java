package placebooks.client.ui.items.frames;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.PlaceBookItemWidgetFactory;


public abstract class PlaceBookItemFrameFactory
{
	public abstract PlaceBookItemFrame createFrame();
	
	public abstract boolean getEditable();
	
	public PlaceBookItemFrame createFrame(PlaceBookItem item)
	{
		PlaceBookItemWidget widget = PlaceBookItemWidgetFactory.createItemWidget(item, getEditable());
		PlaceBookItemFrame frame = createFrame();
		frame.setItemWidget(widget);
		
		return frame;
	}
}