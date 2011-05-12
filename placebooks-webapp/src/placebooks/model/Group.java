package placebooks.model;

import java.util.Collection;
import java.util.Iterator;

public class Group implements Iterable<PlaceBook>
{
	private Collection<PlaceBook> placebooks;

	public void add(final PlaceBook placebook)
	{
		placebooks.add(placebook);
	}

	@Override
	public Iterator<PlaceBook> iterator()
	{
		return placebooks.iterator();
	}
}
