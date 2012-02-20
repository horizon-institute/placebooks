package placebooks.model.json;

import placebooks.model.PlaceBookBinder;

public class PlaceBookBinderDistanceEntry extends PlaceBookBinderOwnedEntry
{
	private final double distance;

	public PlaceBookBinderDistanceEntry(final PlaceBookBinder p, 
										final double distance)
	{
		super(p);
		this.distance = distance;
	}

	public double getDistance()
	{
		return distance;
	}
}
