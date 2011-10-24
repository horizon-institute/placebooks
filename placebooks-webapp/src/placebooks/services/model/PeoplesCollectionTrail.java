package placebooks.services.model;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Class to encapsulate response from Peoples Collection API for a Trail 
 * @author pszmp
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrail
{
	String id;
	String type;

	PeoplesCollectionGeometry geometry;

	PeoplesCollectionTrailProperties properties;

	public PeoplesCollectionTrail()
	{

	}

	public PeoplesCollectionTrail(String id, String type, PeoplesCollectionGeometry geometry, PeoplesCollectionTrailProperties properties)
	{
		this.id = id;
		this.geometry = geometry;
		this.properties = properties;
	}

	public String GetId()
	{
		return id;
	}

	public String GetType()
	{
		return type;
	}

	public PeoplesCollectionGeometry GetPeoplesCollectionGeometry()
	{
		return geometry;
	}

	public Geometry GetGeometry() throws IOException
	{
		return geometry.GetGeometry();
	}

	
	public PeoplesCollectionTrailProperties GetProperties()
	{
		return properties;
	}

}
