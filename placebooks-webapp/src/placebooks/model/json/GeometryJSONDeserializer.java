package placebooks.model.json;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryJSONDeserializer extends JsonDeserializer<Geometry>
{
	@Override
	public Geometry deserialize(JsonParser parser, DeserializationContext context) throws IOException,
			JsonProcessingException
	{
		try
		{
			return new WKTReader().read(parser.getText());
		}
		catch (ParseException e)
		{
			throw new IOException("Parse Error", e);
		}
	}
}
