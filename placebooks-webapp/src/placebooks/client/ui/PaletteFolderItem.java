package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import placebooks.client.resources.Resources;

public class PaletteFolderItem extends PaletteItem implements Iterable<PaletteItem>
{
	private final PaletteFolderItem parent;
	
	private final Collection<PaletteItem> children = new ArrayList<PaletteItem>();
	
	public PaletteFolderItem(String name, PaletteFolderItem parent)
	{
		super();
		this.parent = parent;
		
		text.setText(name);

		image.setResource(Resources.INSTANCE.folder());	
	}
	
	public PaletteFolderItem getParent()
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