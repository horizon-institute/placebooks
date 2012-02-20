package placebooks.client.ui.palette;

import placebooks.client.ui.elements.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PaletteItem
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

	private String name;

	public PaletteItem(final String name)
	{
		this.name = name;
	}

	public Widget createWidget()
	{
		final Widget result = uiBinder.createAndBindUi(this);
		text.setText(name);
		return result;
	}

	public String getName()
	{
		return name;
	}

	public boolean isFolder()
	{
		return false;
	}
}