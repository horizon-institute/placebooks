package placebooks.client.ui.items.frames;

import com.google.gwt.core.client.GWT;

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
		if(widget == null)
		{
			GWT.log("No widget for " + item.getKey());
		}
		PlaceBookItemFrame frame = createFrame();
		frame.setItemWidget(widget);
		
		return frame;
	}
}