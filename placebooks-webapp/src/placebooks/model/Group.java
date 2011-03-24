package placebooks.model;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.annotations.Persistent;

public class Group implements Iterable<PlaceBook>
{
	@Persistent
	private Collection<PlaceBook> placebooks;
	
	public void add(PlaceBook placebook)
	{
		placebooks.add(placebook);
	}
	
	public Iterator<PlaceBook> iterator()
	{
		return placebooks.iterator();
	}
}
