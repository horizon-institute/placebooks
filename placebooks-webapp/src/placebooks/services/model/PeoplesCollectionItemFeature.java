package placebooks.services.model;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to encapsulate response from Peoples Collection API for a Trail
 * 
 * @author pszmp
 * 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionItemFeature
{
	String id;
	String type;

	PeoplesCollectionGeometryPoint geometry;

	PeoplesCollectionProperties properties;

	public PeoplesCollectionItemFeature()
	{

	}

	public PeoplesCollectionItemFeature(final String id, final String type,
			final PeoplesCollectionGeometryPoint geometry, final PeoplesCollectionProperties properties)
	{
		this.id = id;
		this.type = type;
		this.geometry = geometry;
		this.properties = properties;
	}

	public Geometry GetGeometry() throws IOException
	{
		return geometry.GetGeometry();
	}

	public String GetId()
	{
		return id;
	}

	public PeoplesCollectionGeometryPoint GetPeoplesCollectionGeometry()
	{
		return geometry;
	}

	public PeoplesCollectionProperties GetProperties()
	{
		return properties;
	}

	public int GetPropertiesId()
	{
		return properties.GetId();
	}

	public String GetType()
	{
		return type;
	}

}
