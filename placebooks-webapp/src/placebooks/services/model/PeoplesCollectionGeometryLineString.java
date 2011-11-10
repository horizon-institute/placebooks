package placebooks.services.model;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionGeometryLineString
{
	protected static final Logger log = Logger.getLogger(PeoplesCollectionGeometryLineString.class);
		
	private String type;
	private float[][] coordinates;

	public PeoplesCollectionGeometryLineString() {
		
	}

	public PeoplesCollectionGeometryLineString(String type, float[][] coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}

	
	public String GetType() {
		return type;
	}

	public void SetType(String type) {
		this.type = type;
	}

	public float[][] GetCoordinates() {
		return coordinates;
	}

	public void SetCoordinates(float[][] coordinates) {
		this.coordinates = coordinates;
	}

	public Geometry GetGeometry() throws IOException {
		try
		{
			StringBuilder wktStringBuilder = new StringBuilder();
			if(coordinates.length==1)
			{
				log.warn("Converting LineString to Point in Peoplescollection item");
				wktStringBuilder.append("POINT (" + coordinates[0][0] + " " +  coordinates[0][1] + ")");
			}
			else
			{
				wktStringBuilder.append(type.toUpperCase());
				if(coordinates.length==0)
				{
					wktStringBuilder.append(" EMPTY");
				}
				else
				{
					wktStringBuilder.append(" (");
					boolean first = true;
					for(float[] coordinate : coordinates)
					{
						if(first)
						{
							first = false;
						}
						else
						{
							wktStringBuilder.append(", ");
						}
						wktStringBuilder.append(coordinate[0] + " " +  coordinate[1]);
					}
					wktStringBuilder.append(")");
				}
			}
			log.debug("Created WKT string:" + wktStringBuilder.toString());
			return new WKTReader().read(wktStringBuilder.toString());
		}
		catch (ParseException e)
		{
			throw new IOException("WKT Parse Error", e);
		}
	}
}