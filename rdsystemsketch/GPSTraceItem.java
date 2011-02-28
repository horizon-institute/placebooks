package placebooks;

public class GPSTraceItem extends PlaceBookItem
{
	private XMLObject gpx; // Some kind of representatation of the GPS trace---
						   // XML?

	public GPSTraceItem()
	{
		super();
		// Make an empty WebBundleItem
		...
	}

	public GPSTraceItem(int id)
	{
		super(id)
		// Recreate this PlaceBookItem from existing item in db
		...
	}	

	public PersistentItem toPersistentItem()
	{
		// 
	}
}
