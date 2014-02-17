package org.placebooks.client.ui.palette;

import org.placebooks.client.ui.UIMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class PaletteBackItem extends PaletteItem
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private final PaletteFolder parent;
	private final Palette palette;

	public PaletteBackItem(final PaletteFolder parent, final Palette palette)
	{
		super(uiMessages.back());
		this.parent = parent;
		this.palette = palette;
	}

	@Override
	public Widget createWidget()
	{
		final Widget result = super.createWidget();

		image.setVisible(false);

		text.setStyleName(style.backItem());

		// markerImage.setResource(Resources.IMAGES.back_border());
		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				palette.setPaletteFolder(parent);
			}
		});
		return result;
	}

	public PaletteFolder getFolderParent()
	{
		return parent;
	}
}
