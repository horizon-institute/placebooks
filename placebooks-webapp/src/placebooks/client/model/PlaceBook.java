package placebooks.client.model;

import java.util.Collection;

public class PlaceBook
{
	private Collection<PlaceBookItem> items;
	
	public Iterable<PlaceBookItem> getItems()
	{
		return items;
	}
}
