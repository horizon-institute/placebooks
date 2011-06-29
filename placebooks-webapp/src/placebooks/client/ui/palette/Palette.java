package placebooks.client.ui.palette;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookItemDragHandler;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.FlowPanel;

public class Palette extends FlowPanel
{
	private static final String NEW_AUDIO_ITEM = "{\"@class\":\"placebooks.model.AudioItem\",\"sourceURL\":\"http://www.tonycuffe.com/mp3/tailtoddle_lo.mp3\",\"metadata\":{\"title\":\"Audio\"},\"parameters\":{}}";

	private static final String NEW_GPS_ITEM = "{\"@class\":\"placebooks.model.GPSTraceItem\",\"sourceURL\":\"http://www.topografix.com/fells_loop.gpx\",\"metadata\":{\"title\":\"GPS Route\"},\"parameters\":{}}";

	private static final String NEW_IMAGE_ITEM = "{\"@class\":\"placebooks.model.ImageItem\", \"sourceURL\":\"http://farm6.static.flickr.com/5104/5637692627_a6bdf5fccb_z.jpg\",\"metadata\":{\"title\":\"Image\"},\"parameters\":{}}";
	private static final String NEW_TEXT_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Text Block\"},\"parameters\":{},\"text\":\"New Text Block\"}";
	private static final String NEW_VIDEO_ITEM = "{\"@class\":\"placebooks.model.VideoItem\",\"sourceURL\":\"http://www.cs.nott.ac.uk/~ktg/sample_iPod.mp4\",\"metadata\":{\"title\":\"Video\"},\"parameters\":{}}";
	private static final String NEW_WEB_ITEM = "{\"@class\":\"placebooks.model.WebBundleItem\",\"sourceURL\":\"http://www.google.com/\",\"metadata\":{\"title\":\"Web Page\"},\"parameters\":{}}";

	public Palette()
	{
	}

	public void setPalette(final JsArray<PlaceBookItem> items, final PlaceBookItemDragHandler dragHandler)
	{
		final PaletteFolder root = new PaletteFolder("root", null, this);

		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_TEXT_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_IMAGE_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_VIDEO_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_WEB_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_GPS_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookItem.parse(NEW_AUDIO_ITEM), dragHandler));

		for (int index = 0; index < items.length(); index++)
		{
			PaletteFolder folder = root;
			final PlaceBookItem item = items.get(index);
			if (item.hasMetadata("source"))
			{
				folder = root.getFolder(item.getMetadata("source"));
				if (item.hasMetadata("tripName"))
				{
					folder = folder.getFolder(item.getMetadata("tripName"));
				}
				else if (item.hasMetadata("trip"))
				{
					folder = folder.getFolder(item.getMetadata("trip"));
				}
			}
			folder.add(new PalettePlaceBookItem(items.get(index), dragHandler));
		}

		setPaletteFolder(root);
	}

	public void setPaletteFolder(final PaletteFolder folder)
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
}