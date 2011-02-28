package placebooks;

public class ImageItem extends PlaceBookItem
{
	private Image image; 

	public ImageItem()
	{
		super();
		// Make an empty ImageItem
		...
	}

	public ImageItem(int id)
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
