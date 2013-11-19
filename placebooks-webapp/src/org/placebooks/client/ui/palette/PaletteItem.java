package org.placebooks.client.ui.palette;

import org.placebooks.client.ui.widgets.MousePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
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

	interface PaletteStyle extends CssResource
	{
		String backItem();
	}

	private static PaletteItemUiBinder uiBinder = GWT.create(PaletteItemUiBinder.class);

	@UiField
	Image image;

	@UiField
	MousePanel panel;

	@UiField
	Label text;

	@UiField
	PaletteStyle style;

	private String name;

	public PaletteItem(final String name)
	{
		this.name = name;
	}

	public Widget createWidget()
	{
		final Widget result = uiBinder.createAndBindUi(this);
		text.setText(name);
		text.setTitle(name);
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