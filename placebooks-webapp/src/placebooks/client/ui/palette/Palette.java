package placebooks.client.ui.palette;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.PlaceBookHome;
import placebooks.client.ui.elements.PlaceBookController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

public class Palette extends FlowPanel
{
	private static final String NEW_TEXT_HEADER_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Header\"},\"parameters\":{},\"text\":\"<div style='font-size: 25px; font-weight:bold;'>Header</div>\"}";
	private static final String NEW_TEXT_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Body Text\"},\"parameters\":{},\"text\":\"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"}";
	private static final String NEW_TEXT_LIST_ITEM = "{\"@class\":\"placebooks.model.TextItem\",\"metadata\":{\"title\":\"Bulleted List\"},\"parameters\":{},\"text\":\"<ul style='margin: 3px 0px;'><li>List Item</li><li>List Item</li><li>List Item</li></ul>\"}";

	// private static final String NEW_WEB_ITEM =
	// "{\"@class\":\"placebooks.model.WebBundleItem\",\"sourceURL\":\"http://www.google.com/\",\"metadata\":{\"title\":\"Web Page\"},\"parameters\":{}}";

	private static final String NEW_AUDIO_ITEM = "{\"@class\":\"placebooks.model.AudioItem\",\"metadata\":{\"title\":\"Audio\"},\"parameters\":{}}";
	private static final String NEW_IMAGE_ITEM = "{\"@class\":\"placebooks.model.ImageItem\",\"metadata\":{\"title\":\"Image\"},\"parameters\":{}}";
	private static final String NEW_VIDEO_ITEM = "{\"@class\":\"placebooks.model.VideoItem\",\"metadata\":{\"title\":\"Video\"},\"parameters\":{}}";

	private static final String NEW_GPS_ITEM = "{\"@class\":\"placebooks.model.GPSTraceItem\",\"metadata\":{\"title\":\"Map\"},\"parameters\":{}}";

	private PaletteFolder currentFolder = null;

	private PlaceBookController controller;
	private PlaceController placeController;
	
	private final Timer timer = new Timer()
	{
		@Override
		public void run()
		{
			updatePalette();
		}
	};

	
	public Palette()
	{
	}
	
	public void setControllers(final PlaceBookController controller, final PlaceController placeController)
	{
		this.controller = controller;
		this.placeController = placeController;
		setPalette(null);
		updatePalette();
		timer.scheduleRepeating(120000);		
	}
		
	public void updatePalette()
	{
		PlaceBookService.getPaletteItems(new AbstractCallback()
		{
			@Override
			public void failure(final Request request, final Response response)
			{
				if (response.getStatusCode() == 401)
				{
					placeController.goTo(new PlaceBookHome());
				}
			}

			@Override
			public void success(final Request request, final Response response)
			{
				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
				setPalette(items);
			}

			@Override
			public void onError(Request request, Throwable throwable)
			{
				setPalette(null);
				super.onError(request, throwable);
			}
			
		});
	}	

	private void setPalette(final JsArray<PlaceBookItem> items)
	{
		final PaletteFolder root = new PaletteFolder("root", null, this);

		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_HEADER_ITEM),
		                                  controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_ITEM), controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_TEXT_LIST_ITEM), controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_IMAGE_ITEM), controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_VIDEO_ITEM), controller));
		// root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class,
		// NEW_WEB_ITEM), controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_GPS_ITEM), controller));
		root.add(new PalettePlaceBookItem(PlaceBookService.parse(PlaceBookItem.class, NEW_AUDIO_ITEM), controller));

		if(items != null)
		{
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
				folder.add(new PalettePlaceBookItem(items.get(index), controller));
			}
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
		try
		{
			clear();
		}
		catch (final Exception e)
		{
			GWT.log(e.getMessage(), e);
		}
		for (final PaletteItem paletteItem : folder)
		{
			try
			{
				add(paletteItem.createWidget());
			}
			catch (final Exception e)
			{
				GWT.log(e.getMessage(), e);
			}
		}
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
}