package placebooks.model.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryJSONSerializer extends JsonSerializer<Geometry>
{
	@Override
	public void serialize(final Geometry geom, final JsonGenerator jgen, final SerializerProvider arg2)
			throws IOException, JsonProcessingException
	{
		jgen.writeString(geom.toText());
	}
}
