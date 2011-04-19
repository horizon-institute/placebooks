package placebooks.model.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import placebooks.model.User;

public class UserKeyJSONSerializer extends JsonSerializer<User>
{
	@Override
	public void serialize(final User user, final JsonGenerator jgen, final SerializerProvider arg2)
			throws IOException, JsonProcessingException
	{
		jgen.writeString(user.getKey());
	}
}
