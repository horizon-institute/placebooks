package placebooks.client.ui.widgets;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;

public class AnchorPanel extends ComplexPanel
{
	/**
	 * Creates an empty flow panel.
	 */
	public AnchorPanel()
	{
		setElement(DOM.createAnchor());
	}

	protected void setHref(String href)
	{
		getAnchorElement().setHref(href);
	}

	private AnchorElement getAnchorElement()
	{
		return AnchorElement.as(getElement());
	}
}