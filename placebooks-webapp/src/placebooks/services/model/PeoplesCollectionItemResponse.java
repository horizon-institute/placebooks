package placebooks.services.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

/**
 * Class to encapsulate response from peoples collection trip media/items
 * 
 * @author pszmp
 * 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionItemResponse
{
	protected int total_objects;
	protected PeoplesCollectionItemFeature[] features;
	protected String type;
	protected String page;
	protected int per_page;

	public PeoplesCollectionItemResponse()
	{
	}

	public PeoplesCollectionItemResponse(final int id, final int total_objects,
			final PeoplesCollectionItemFeature[] features, final String type, final String page, final int per_page)
	{
		this.total_objects = total_objects;
		this.features = features;
		this.type = type;
		this.page = page;
		this.per_page = per_page;
	}

	public PeoplesCollectionItemFeature[] getFeatures()
	{
		return features;
	}

	public String GetPage()
	{
		return page;
	}

	public int GetPerPage()
	{
		return per_page;
	}

	public int GetTotalObjects()
	{
		return total_objects;
	}

	public String GetType()
	{
		return type;
	}

}
