package placebooks.services.model;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.vividsolutions.jts.geom.Geometry;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailResponse
{
	String id;
	String type;
	PeoplesCollectionGeometryLineString geometry;
	PeoplesCollectionTrailProperties properties;
	PeoplesCollectionTrailCRS crs;

	public PeoplesCollectionTrailResponse()
	{

	}

	public PeoplesCollectionTrailResponse(final String id, final String type,
			final PeoplesCollectionGeometryLineString geometry, final PeoplesCollectionTrailProperties properties,
			final PeoplesCollectionTrailCRS crs)
	{
		this.id = id;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
		this.crs = crs;
	}

	public PeoplesCollectionTrailCRS GetCrs()
	{
		return crs;
	}

	public Geometry GetGeometry() throws IOException
	{
		return geometry.GetGeometry();
	}

	public String GetId()
	{
		return id;
	}

	public PeoplesCollectionTrailProperties GetProperties()
	{
		return properties;
	}

	public int GetPropertiesId()
	{
		return properties.GetTrailId();
	}

	public String GetType()
	{
		return type;
	}

}
