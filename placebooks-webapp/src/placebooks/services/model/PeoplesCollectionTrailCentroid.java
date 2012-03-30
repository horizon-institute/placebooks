package placebooks.services.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailCentroid {
	protected String id;
	protected double x;
	protected double y;

	public PeoplesCollectionTrailCentroid () 
	{ }
	
	public PeoplesCollectionTrailCentroid(String id, double x, double y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public String GetID()
	{
		return id;
	}

	public double GetX()
	{
		return x;
	}

	public double GetY()
	{
		return y;
	}
}