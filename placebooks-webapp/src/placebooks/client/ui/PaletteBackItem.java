package placebooks.client.ui;

import placebooks.client.resources.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class PaletteBackItem extends PaletteItem
{
	private final PaletteFolderItem parent;

	public PaletteBackItem(final String name, final PaletteFolderItem parent, final PlaceBookPalette palette)
	{
		super();
		this.parent = parent;

		text.setText(name);

		image.setResource(Resources.INSTANCE.folder());
		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				palette.setPaletteFolder(parent);
			}
		});
	}

	public PaletteFolderItem getFolderParent()
	{
		return parent;
	}
}
