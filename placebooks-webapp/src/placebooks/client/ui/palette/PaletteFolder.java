package placebooks.client.ui.palette;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import placebooks.client.Resources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public class PaletteFolder extends PaletteItem implements Iterable<PaletteItem>
{
	private final Collection<PaletteItem> children = new ArrayList<PaletteItem>();

	private final Palette palette;

	private final PaletteFolder parent;

	public PaletteFolder(final String name, final PaletteFolder parent, final Palette palette)
	{
		super(name);
		this.parent = parent;
		this.palette = palette;
	}

	@Override
	public Widget createWidget()
	{
		Widget result = super.createWidget();

		
		if ((parent != null))
		{
			// Fix to avoid/remove multiple back folder icons
			boolean backFound = false;
			for(PaletteItem item : children )
			{
				if(item instanceof PaletteBackItem)
				{
					if(backFound)
					{
						children.remove(item);
					}
					backFound = true;
				}
			}
			if(!backFound)
			{
				add(new PaletteBackItem("Back", parent, palette));
			}
		}
	
		image.setResource(Resources.IMAGES.pallette_folder());
		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				palette.setPaletteFolder(PaletteFolder.this);
			}
		});
		
		return result;
	}

	public void add(final PaletteItem item)
	{
		children.add(item);
	}

	public PaletteFolder getFolder(final String name)
	{
		for (final PaletteItem item : children)
		{
			if (item instanceof PaletteFolder && item.getName().equals(name)) { return (PaletteFolder) item; }
		}
		final PaletteFolder item = new PaletteFolder(name, this, palette);
		children.add(item);
		return item;
	}

	public PaletteFolder getParentFolder()
	{
		return parent;
	}

	@Override
	public boolean isFolder()
	{
		return true;
	}

	@Override
	public Iterator<PaletteItem> iterator()
	{
		return children.iterator();
	}
}