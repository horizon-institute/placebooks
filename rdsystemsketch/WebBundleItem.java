package placebooks;

public class WebBundleItem extends PlaceBookItem
{

	private Image thumbnail;
	private XMLObject htmlPage; // HTML from the mirrored page
	private ArrayList<Image> images; // Associated images 

	public WebBundleItem()
	{
		super();
		// Make an empty WebBundleItem
		...
	}

	public WebBundleItem(int id)
	{
		super(id)
		// Recreate this PlaceBookItem from existing item in db
		...
	}	

	// A thumbnail preview image of the webpage - rendered somehow and stored 
	// here
	public Image getPreview()
	{

	}

	// An alternative preview - a FaceBook style header text plus image drawn 
	// from the webpage in question
	public WebPreviewBundle getWebPreview()
	{

	}

	public static class WebPreviewBundle
	{
		private String headerText;
		private Image headerImage;
	}
	

	public PersistentItem toPersistentItem()
	{
		// 
	}
}
