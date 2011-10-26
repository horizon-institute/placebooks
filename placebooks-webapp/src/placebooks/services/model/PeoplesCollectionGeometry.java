package placebooks.services.model;

import java.io.IOException;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

//@JsonSerialize(using = placebooks.model.json.PeoplesCollectionGeometryJSONSerializer.class)
//@JsonDeserialize(using = placebooks.model.json.PeoplesCollectionGeometryJSONDeserializer.class)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionGeometry
{
	private String type;
	private double[] coordinates;

	public PeoplesCollectionGeometry() {
		
	}

	public PeoplesCollectionGeometry(String type, double[] coordinates) {
		this.type = type;
		this.coordinates = coordinates;
	}

	
	public String GetType() {
		return type;
	}

	public void SetType(String type) {
		this.type = type;
	}

	public double[] GetCoordinates() {
		return coordinates;
	}

	public void SetCoordinates(double[] coordinates) {
		this.coordinates = coordinates;
	}

	public Geometry GetGeometry() throws IOException {
		try
		{
			return new WKTReader().read(type + " " + coordinates.toString());
		}
		catch (ParseException e)
		{
			throw new IOException("Parse Error", e);
		}
	}
}