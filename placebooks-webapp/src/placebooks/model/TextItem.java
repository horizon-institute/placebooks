package placebooks.model;

import java.net.URL;
import javax.jdo.annotations.*;
import com.vividsolutions.jts.geom.Geometry;


@PersistenceCapable
public class TextItem extends PlaceBookItem
{
	@Persistent
	private String text; 

	public TextItem() { }

	public TextItem(int owner, Geometry geom, URL sourceURL, String text)
	{
		super(owner, geom, sourceURL);
		this.text = text;
	}


}
