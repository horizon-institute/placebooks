package placebooks.client.ui.items.frames;

import placebooks.client.resources.Resources;

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
		rootPanel = widgetPanel;
		widgetPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
	}
}
