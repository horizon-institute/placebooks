package placebooks;

import java.io.File;

public class VideoItem extends PlaceBookItem
{
	// Videos are stored on disk, not database
	private File video; 

	public VideoItem()
	{
		super();
		// Make an empty VideoItem
		...
	}

	public VideoItem(int id)
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
