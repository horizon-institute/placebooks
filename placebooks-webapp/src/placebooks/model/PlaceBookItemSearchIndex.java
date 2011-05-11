package placebooks.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Inheritance;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class PlaceBookItemSearchIndex extends SearchIndex
{
	@Persistent
	private PlaceBookItem item;

	public PlaceBookItemSearchIndex() { super(); }

	public PlaceBookItem getPlaceBookItem() { return item; }

	public void setPlaceBookItem(PlaceBookItem item) 
	{ 
		this.item = item; 
	}

}

