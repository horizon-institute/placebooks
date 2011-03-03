package placebooks.model;

import javax.jdo.annotations.*;
import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
public class TextItem extends PlaceBookItem
{
	@Persistent
	private String text; 

	public TextItem(int owner, Geometry geom, String text)
	{
		super(owner, geom);
		this.text = text;
	}

	// Restore this PlaceBookItem from existing item in db
	public TextItem(String key)
	{
		super(key);
	}	

}
