package placebooks.client.ui.items.frames;

import placebooks.client.Resources;

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
		public boolean isEditable()
		{
			return false;
		}
	};

	public PlaceBookItemBlankFrame()
	{
		rootPanel = widgetPanel;
		widgetPanel.setStyleName(Resources.STYLES.style().widgetPanel());
	}
}
