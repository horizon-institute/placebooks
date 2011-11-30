package placebooks.client.ui.elements;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class PlaceBookCanvas extends PlaceBookPages
{
	interface Style extends CssResource
	{
		String canvas();
	    String page();
	    String pageInvisible();
	}

	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookCanvas.css")
		Style style();
	}
	
	private static final Bundle STYLES = GWT.create(Bundle.class);

	private final FlowPanel panel = new FlowPanel();

	public PlaceBookCanvas()
	{
		initWidget(panel);
		STYLES.style().ensureInjected();
		setStyleName(STYLES.style().canvas());
	}

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