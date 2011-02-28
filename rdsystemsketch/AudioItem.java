package placebooks;

import java.io.File;

public class AudioItem extends PlaceBookItem
{
	private File audio; 

	public AudioItem()
	{
		super();
		// Make an empty AudioItem
		...
	}

	public AudioItem(int id)
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
