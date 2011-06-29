package placebooks.client.ui;

import placebooks.client.resources.Resources;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookItemSimpleFrame extends PlaceBookItemFrame2
{
	private final SimplePanel widgetPanel = new SimplePanel();

	public PlaceBookItemSimpleFrame()
	{
		final SimplePanel rootPanel = new SimplePanel();
		initWidget(rootPanel);
		rootPanel.setStyleName(Resources.INSTANCE.style().widgetPanel());
		widgetPanel.getElement().getStyle().setMargin(5, Unit.PX);
		widgetPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		rootPanel.add(widgetPanel);		
	}

	@Override
	protected void onSetItem()
	{
		widgetPanel.clear();
		widgetPanel.add(itemWidget);
	}
}
