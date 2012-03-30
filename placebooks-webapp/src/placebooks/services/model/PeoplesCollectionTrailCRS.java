package placebooks.services.model;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailCRS
{
	protected String type;
	protected HashMap<String, String> properties;

	PeoplesCollectionTrailCRS()
	{ }

	public PeoplesCollectionTrailCRS(String type, HashMap<String, String> properies)
	{
		this.type = type;
		this.properties = properies;
	}

	public String GetType()
	{
		return type;
	}

	public HashMap<String, String> GetProperties()
	{
		return properties;
	}
}