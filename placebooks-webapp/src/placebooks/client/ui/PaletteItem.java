package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PaletteItem extends Composite
{
	interface PaletteItemUiBinder extends UiBinder<Widget, PaletteItem>
	{
	}

	private static PaletteItemUiBinder uiBinder = GWT.create(PaletteItemUiBinder.class);

	@UiField
	MousePanel panel;

	@UiField
	Label text;

	@UiField
	Image image;

	private PlaceBookItem item;
	
	public PaletteItem(final String json, final String text)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.text.setText(text);
		item = PlaceBookItem.parse(json);
		
		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			image.setResource(Resources.INSTANCE.text());
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			image.setResource(Resources.INSTANCE.picture());
		}
	}
	
	PlaceBookItem getPlaceBookItem()
	{
		return item;
	}

	void addDragStartHandler(final MouseDownHandler handler)
	{
		panel.addMouseDownHandler(handler);
	}
}
