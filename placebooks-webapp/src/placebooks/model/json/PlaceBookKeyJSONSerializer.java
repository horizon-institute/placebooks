package placebooks.model.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import placebooks.model.PlaceBook;

public class PlaceBookKeyJSONSerializer extends JsonSerializer<PlaceBook>
{
	@Override
	public void serialize(final PlaceBook placebook, final JsonGenerator jgen, final SerializerProvider arg2)
			throws IOException, JsonProcessingException
	{
		jgen.writeString(placebook.getKey());
	}
}
