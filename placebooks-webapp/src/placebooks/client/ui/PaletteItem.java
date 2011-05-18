package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.json.client.JSONObject;
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
	Image image;

	@UiField
	MousePanel panel;

	@UiField
	Label text;
	
	private final PlaceBookItem item;

	public PaletteItem(PlaceBookItem placeBookItem)
	{
		initWidget(uiBinder.createAndBindUi(this));
		this.text.setText(placeBookItem.getMetadata("title"));
		item = placeBookItem;

		if (item.getClassName().equals("placebooks.model.TextItem"))
		{
			image.setResource(Resources.INSTANCE.text());
		}
		else if (item.getClassName().equals("placebooks.model.ImageItem"))
		{
			image.setResource(Resources.INSTANCE.picture());
		}
		else if (item.getClassName().equals("placebooks.model.VideoItem"))
		{
			image.setResource(Resources.INSTANCE.movies());
		}		
	}

	void addDragStartHandler(final MouseDownHandler handler)
	{
		panel.addMouseDownHandler(handler);
	}
	
	PlaceBookItem createItem()
	{
		// TODO is there a better way to clone item?
		return PlaceBookItem.parse(new JSONObject(item).toString());
	}
}
