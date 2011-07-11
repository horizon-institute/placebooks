package placebooks.client.ui.items.frames;

import placebooks.client.resources.Resources;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookItemBlankFrame extends PlaceBookItemFrame
{
	public static PlaceBookItemFrameFactory FACTORY = new PlaceBookItemFrameFactory()
	{
		@Override
		public PlaceBookItemFrame createFrame()
		{
			return new PlaceBookItemBlankFrame();
		}

		@Override
		public boolean getEditable()
		{
			return false;
		}		
	};
	
	public PlaceBookItemBlankFrame()
	{
		rootPanel = new SimplePanel();
		rootPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
		widgetPanel.getElement().getStyle().setMargin(5, Unit.PX);
		widgetPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		rootPanel.add(widgetPanel);		
	}
}
