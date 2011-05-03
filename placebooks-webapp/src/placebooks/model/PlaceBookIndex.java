package placebooks.model;

import java.util.Set;
import java.util.HashSet;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Extension;

@PersistenceCapable(identityType=IdentityType.DATASTORE)
@Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
public class PlaceBookIndex
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private Set<String> index = new HashSet<String>();

	@Persistent
	private PlaceBook placebook;

	public PlaceBookIndex() { }

	public Set<String> getIndex() { return index; }
	public void setIndex(Set<String> index) { this.index = index; }
	public void add(String element) { index.add(element); }

	public PlaceBook getPlaceBook() { return placebook; }
	public void setPlaceBook(PlaceBook placebook) 
	{ 
		this.placebook = placebook; 
	}

}

