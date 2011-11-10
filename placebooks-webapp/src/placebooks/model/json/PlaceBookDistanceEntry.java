package placebooks.model.json;

import placebooks.model.PlaceBook;

public class PlaceBookDistanceEntry extends PlaceBookEntry
{
	private final double distance;
	
	private final String ownerName;

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
