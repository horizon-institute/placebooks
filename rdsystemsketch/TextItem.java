package placebooks;

public class TextItem extends PlaceBookItem
{
	private String text; 

	public TextItem()
	{
		super();
		// Make an empty WebBundleItem
		...
	}

	public TextItem(int id)
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
