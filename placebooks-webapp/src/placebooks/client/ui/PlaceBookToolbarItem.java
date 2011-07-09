package placebooks.client.ui;

import placebooks.client.resources.Resources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class PlaceBookToolbarItem extends FlowPanel
{
	private final InlineLabel label = new InlineLabel();
	private final Image image = new Image();

	public PlaceBookToolbarItem(final String text, final ImageResource imageRes, final ClickHandler clickHandler)
	{
		setStyleName(Resources.INSTANCE.style().toolbarItem());
		if(imageRes != null)
		{
			add(image);
			image.setResource(imageRes);
			image.getElement().getStyle().setMarginRight(5, Unit.PX);
			image.getElement().getStyle().setMarginBottom(-2, Unit.PX);			
		}
		label.setText(text);		
		add(label);
		addDomHandler(clickHandler, ClickEvent.getType());
	}
}
