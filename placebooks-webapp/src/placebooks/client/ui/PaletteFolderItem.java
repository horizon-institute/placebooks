package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import placebooks.client.resources.Resources;

public class PaletteFolderItem extends PaletteItem implements Iterable<PaletteItem>
{
	private final PaletteFolderItem parent;
	
	private final Collection<PaletteItem> children = new ArrayList<PaletteItem>();
	
	public PaletteFolderItem(String name, PaletteFolderItem parent, final PlaceBookPalette palette)
	{
		super();
		this.parent = parent;
		
		text.setText(name);

		if(parent != null)
		{
			add(new PaletteBackItem("Back", parent, palette));
		}
		
		image.setResource(Resources.INSTANCE.folder());	
		panel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				palette.setPaletteFolder(PaletteFolderItem.this);
			}
		});		
	}
	
	public PaletteFolderItem getFolderParent()
	{
		return parent;
	}

	public Iterator<PaletteItem> iterator()
	{
		return children.iterator();
	}
	
	public void add(PaletteItem item)
	{
		children.add(item);
	}
	
	public boolean isFolder()
	{
		return true;
	}
}