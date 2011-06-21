package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookPalette extends FlowPanel
{
	private static final String NEW_AUDIO_ITEM = "{\"@class\":\"placebooks.model.AudioItem\",\"sourceURL\":\"http://www.tonycuffe.com/mp3/tailtoddle_lo.mp3\",\"metadata\":{\"title\":\"Test Audio\"},\"parameters\":{}}";

	private static final String NEW_GPS_ITEM = "{\"@class\":\"placebooks.model.GPSTraceItem\",\"sourceURL\":\"http://www.topografix.com/fells_loop.gpx\",\"metadata\":{\"title\":\"Test Route\"},\"parameters\":{}}";

	private static final String NEW_IMAGE_ITEM = "{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\",\"metadata\":{\"title\":\"Image Item\"},\"parameters\":{}}";
	private static final String NEW_TEXT_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Text Item\"},\"parameters\":{},\"text\":\"New Text Block\"}";
	private static final String NEW_VIDEO_ITEM = "{\"@class\":\"placebooks.model.VideoItem\",\"sourceURL\":\"http://www.cs.nott.ac.uk/~ktg/sample_iPod.mp4\",\"metadata\":{\"title\":\"Video Item\"},\"parameters\":{}}";
	private static final String NEW_WEB_ITEM = "{\"@class\":\"placebooks.model.WebBundleItem\",\"sourceURL\":\"http://www.google.com/\",\"metadata\":{\"title\":\"Web Bundle\"},\"parameters\":{}}";
	
	public PlaceBookPalette()
	{
	}	
	
	public void setPaletteFolder(final PaletteFolderItem folder)
	{
		clear();
		if (folder.getParent() != null)
		{
			// TODO Show back button
		}

		for (final PaletteItem paletteItem : folder)
		{
			add(paletteItem);
		}
	}
	
	public void setPalette(final JsArray<PlaceBookItem> items, PlaceBookEditor editor)
	{
		final PaletteFolderItem root = new PaletteFolderItem("root", null, this);

		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_TEXT_ITEM), editor));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_IMAGE_ITEM), editor));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_VIDEO_ITEM), editor));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_WEB_ITEM), editor));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_GPS_ITEM), editor));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_AUDIO_ITEM), editor));

		for (int index = 0; index < items.length(); index++)
		{
			// TODO Organize
			root.add(new PalettePlaceBookItem(items.get(index), editor));
		}

		root.add(new PaletteFolderItem("Test", root, this));
		
		setPaletteFolder(root);
	}	
}