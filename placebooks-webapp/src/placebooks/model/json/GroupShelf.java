package placebooks.model.json;

import java.util.ArrayList;
import java.util.List;

import placebooks.model.PlaceBookBinder;
import placebooks.model.PlaceBookGroup;

public class GroupShelf extends Shelf
{
	private PlaceBookGroup group;

	GroupShelf()
	{
		
	}
	
	public GroupShelf(final PlaceBookGroup group)
	{
		super();
		this.group = group;

		final List<ShelfEntry> entries = new ArrayList<ShelfEntry>();
		for (final PlaceBookBinder pb : group.getPlaceBooks())
		{
			entries.add(new PlaceBookBinderEntry(pb));
		}
		setEntries(entries);
	}

	public PlaceBookGroup getGroup()
	{
		return group;
	}
}
