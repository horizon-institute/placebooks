package placebooks.client.ui.palette;

import placebooks.client.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class PaletteBackItem extends PaletteItem
{
	private final PaletteFolder parent;
	private final Palette palette;
	
	public PaletteBackItem(final String name, final PaletteFolder parent, final Palette palette)
	{
		super(name);
		this.parent = parent;
		this.palette = palette;
	}

	@Override
	public Widget createWidget()
	{
		Widget result = super.createWidget();

		image.setResource(Resources.IMAGES.pallette_folder());
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
