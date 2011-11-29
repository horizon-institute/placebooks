package placebooks.model.json;

import placebooks.model.PlaceBookBinder;

public class PlaceBookBinderDistanceEntry extends PlaceBookBinderEntry
{
	private final double distance;
	
	private final String ownerName;

	public PlaceBookBinderDistanceEntry(final PlaceBookBinder p, 
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
