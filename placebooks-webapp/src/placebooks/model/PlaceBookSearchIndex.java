package placebooks.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Inheritance;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
public class PlaceBookSearchIndex extends SearchIndex
{
	@Persistent
	private PlaceBook placebook;

	public PlaceBookSearchIndex() { super(); }

	public PlaceBook getPlaceBook() { return placebook; }

	public void setPlaceBook(PlaceBook placebook) 
	{ 
		this.placebook = placebook; 
	}

}

