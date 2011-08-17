package placebooks.client.ui.items.frames;

import placebooks.client.resources.Resources;

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
		widgetPanel.setStyleName(Resources.INSTANCE.style().widgetInnerPanel());
		rootPanel.add(widgetPanel);
	}
}
