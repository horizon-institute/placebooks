package placebooks.model.json;

import placebooks.model.PlaceBookItem;

import org.codehaus.jackson.annotate.JsonProperty;

public class PlaceBookItemDistanceEntry extends PlaceBookItemEntry
{
	@JsonProperty	
	private double distance;
	
	private String ownerName;

	public PlaceBookItemDistanceEntry(final PlaceBookItem p, 
									  final double distance)
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
