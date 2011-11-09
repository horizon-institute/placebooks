package placebooks.services.model;


import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.vividsolutions.jts.geom.Geometry;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailResponse {
	String id;
	String type;
	PeoplesCollectionGeometryLineString geometry;
	PeoplesCollectionTrailProperties properties;
	PeoplesCollectionTrailCRS crs;

	public PeoplesCollectionTrailResponse()
	{
	
	}
	
	public PeoplesCollectionTrailResponse(String id, String type, PeoplesCollectionGeometryLineString geometry, PeoplesCollectionTrailProperties properties, PeoplesCollectionTrailCRS crs)
	{
		this.id = id;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
		this.crs = crs;
	}
	
	public String GetId()
	{
		return id;
	}
	
	public String GetType()
	{
		return type;
	}
	
	public Geometry GetGeometry() throws IOException
	{
		return geometry.GetGeometry();
	}
	
	public PeoplesCollectionTrailProperties GetProperties()
	{
		return properties;
	}
	
	public PeoplesCollectionTrailCRS GetCrs()
	{
		return crs;
	}
	
}
