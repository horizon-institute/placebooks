package placebooks.client.ui.palette;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.FlowPanel;

public class Palette extends FlowPanel
{
	private static final String NEW_TEXT_HEADER_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Header\"},\"parameters\":{},\"text\":\"<div style='font-size: 25px; font-weight:bold;'>Header</div>\"}";
	private static final String NEW_TEXT_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Body Text\"},\"parameters\":{},\"text\":\"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"}";
	private static final String NEW_TEXT_LIST_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Bulleted List\"},\"parameters\":{},\"text\":\"<ul style='margin: 3px 0px;'><li>List Item</li><li>List Item</li><li>List Item</li></ul>\"}";

	//private static final String NEW_WEB_ITEM = "{\"@class\":\"placebooks.model.WebBundleItem\",\"sourceURL\":\"http://www.google.com/\",\"metadata\":{\"title\":\"Web Page\"},\"parameters\":{}}";

	private static final String NEW_AUDIO_ITEM = "{\"@class\":\"placebooks.model.AudioItem\",\"metadata\":{\"title\":\"Audio\"},\"parameters\":{}}";
	private static final String NEW_IMAGE_ITEM = "{\"@class\":\"placebooks.model.ImageItem\",\"metadata\":{\"title\":\"Image\"},\"parameters\":{}}";
	private static final String NEW_VIDEO_ITEM = "{\"@class\":\"placebooks.model.VideoItem\",\"metadata\":{\"title\":\"Video\"},\"parameters\":{}}";

	private static final String NEW_GPS_ITEM = "{\"@class\":\"placebooks.model.GPSTraceItem\",\"sourceURL\":\""
			+ PlaceBookService.getHostURL() + "example/example.gpx\",\"hash\":\"b13ada21dd8eda0c3bcc5019045a3c23\",\"metadata\":{\"title\":\"GPS Route\"},\"parameters\":{}}";


	private PaletteFolder currentFolder = null;

	public Palette()
	{
	}

	private PaletteFolder findFolder(final PaletteFolder root, final PaletteFolder folder)
	{
		final List<PaletteFolder> path = getPath(folder);

		PaletteFolder current = root;
		for (final PaletteFolder pathElement : path)
		{
			final PaletteFolder equiv = current.getFolder(pathElement.getName());
			if (equiv != null)
			{
				current = equiv;
			}
			else
			{
				break;
			}
		}
		return current;
	}

	private List<PaletteFolder> getPath(final PaletteFolder folder)
	{
		final List<PaletteFolder> path = new ArrayList<PaletteFolder>();

		PaletteFolder current = folder;
		while (current.getParentFolder() != null)
		{
			path.add(0, current);
			current = current.getParentFolder();
		}

		return path;
	}

	public void setPalette(final JsArray<PlaceBookItem> items, final PlaceBookController dragHandler)
	{
		final PaletteFolder root = new PaletteFolder("root", null, this);

		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_HEADER_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_LIST_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_IMAGE_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_VIDEO_ITEM), dragHandler));
		//root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_WEB_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_GPS_ITEM), dragHandler));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_AUDIO_ITEM), dragHandler));

		for (int index = 0; index < items.length(); index++)
		{
			PaletteFolder folder = root;
			final PlaceBookItem item = items.get(index);
			if (item.hasMetadata("source"))
			{
				folder = root.getFolder(item.getMetadata("source"));
				if (item.hasMetadata("trip_name"))
				{
					folder = folder.getFolder(item.getMetadata("trip_name"));
				}
				else if (item.hasMetadata("trip"))
				{
					folder = folder.getFolder(item.getMetadata("trip"));
				}
			}
			folder.add(new PalettePlaceBookItem(items.get(index), dragHandler));
		}

		if (currentFolder == null)
		{
			setPaletteFolder(root);
		}
		else
		{
			final PaletteFolder newFolder = findFolder(root, currentFolder);
			final int scrollOffset = getElement().getScrollTop();
			setPaletteFolder(newFolder);
			getElement().setScrollTop(scrollOffset);
		}
	}

	public void setPaletteFolder(final PaletteFolder folder)
	{
		currentFolder = folder;
		clear();
		for (final PaletteItem paletteItem : folder)
		{
			add(paletteItem.createWidget());
		}
	}
}