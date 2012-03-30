package placebooks.model.json;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryJSONDeserializer extends JsonDeserializer<Geometry>
{
	private final Logger logger = Logger.getLogger(GeometryJSONDeserializer.class.getName());

	@Override
	public Geometry deserialize(final JsonParser parser, final DeserializationContext context) throws IOException,
			JsonProcessingException
	{
		try
		{
			return new WKTReader().read(parser.getText());
		}
		catch (final Throwable e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
}
