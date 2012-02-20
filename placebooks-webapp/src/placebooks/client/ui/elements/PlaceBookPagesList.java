package placebooks.client.ui.elements;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class PlaceBookPagesList extends PlaceBookPages
{
	private final FlowPanel panel = new FlowPanel();

	public PlaceBookPagesList()
	{
		initWidget(panel);
	}

	@Override
	public void resized()
	{
		for (final PlaceBookPage page : pages)
		{
			final double panelHeight = page.getOffsetWidth() * 2 / 3;
			page.setHeight(panelHeight + "px");
			page.reflow();
		}
	}

	@Override
	protected Panel getPagePanel()
	{
		return panel;
	}
}