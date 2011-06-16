package placebooks.client.ui;

import placebooks.client.ui.widget.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
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
	Image image;

	@UiField
	MousePanel panel;

	@UiField
	Label text;

	public PaletteItem()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}	

	void addDragStartHandler(final MouseDownHandler handler)
	{
		panel.addMouseDownHandler(handler);
	}
	
	void addClickHander(final ClickHandler handler)
	{
		panel.addClickHandler(handler);
	}
	
	public boolean isFolder()
	{
		return false;
	}
}