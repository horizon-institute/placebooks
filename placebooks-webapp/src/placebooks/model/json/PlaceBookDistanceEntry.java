package placebooks.model.json;

import placebooks.model.PlaceBook;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlaceBookDistanceEntry extends PlaceBookEntry
{
	@JsonProperty	
	private double distance;
	
	private String ownerName;

	public PlaceBookDistanceEntry(final PlaceBook p, final double distance)
	{
		super(p);
		this.distance = distance;
		this.ownerName = p.getOwner().getName(); 
	}

	public double getDistance()
	{
		return distance;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
}
