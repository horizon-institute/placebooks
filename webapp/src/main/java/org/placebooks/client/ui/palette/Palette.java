package org.placebooks.client.ui.palette;

import java.util.ArrayList;
import java.util.List;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.views.DragController;
import org.wornchaos.client.server.AsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

public class Palette extends FlowPanel
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final List<Item> paletteItems = new ArrayList<Item>();
	
	static
	{
		Item item = new Item();
		item.setType(Type.TextItem);
		item.getMetadata().put("title", uiMessages.header());
		item.setText("<div style='font-size: 25px; font-weight:bold;'>Header</div>");
		
		paletteItems.add(item);
		
		item = new Item();
		item.setType(Type.TextItem);
		item.getMetadata().put("title", uiMessages.bodyText());		
		item.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
		
		paletteItems.add(item);
		
		item = new Item();
		item.setType(Type.TextItem);
		item.getMetadata().put("title", uiMessages.bulletedText());		
		item.setText("<ul style='margin: 3px 0px;'><li>List Item</li><li>List Item</li><li>List Item</li></ul>");
		
		paletteItems.add(item);
		
		item = new Item();
		item.setType(Type.AudioItem);
		item.getMetadata().put("title", uiMessages.audio());
		
		paletteItems.add(item);
		
		item = new Item();
		item.setType(Type.VideoItem);
		item.getMetadata().put("title", uiMessages.video());
		
		paletteItems.add(item);
		
		item = new Item();
		item.setType(Type.ImageItem);
		item.getMetadata().put("title", uiMessages.image());
		
		paletteItems.add(item);		
		
		item = new Item();
		item.setType(Type.GPSTraceItem);
		item.getMetadata().put("title", uiMessages.map());
		
		paletteItems.add(item);		
	}

	private PaletteFolder currentFolder = null;

	private DragController controller;

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

	public void setDragController(final DragController controller)
	{
		this.controller = controller;
		setPalette(null);
		updatePalette();
		timer.scheduleRepeating(60000);
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

	public void stop()
	{
		timer.cancel();
	}

	public void updatePalette()
	{
		PlaceBooks.getServer().getPaletteItems(new AsyncCallback<Iterable<Item>>()
		{
			@Override
			public void onSuccess(Iterable<Item> items)
			{
				setPalette(items);				
			}
		});
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

	private void setPalette(final Iterable<Item> items)
	{
		final PaletteFolder root = new PaletteFolder("root", null, this);

		for(Item item: paletteItems)
		{
			root.add(new PalettePlaceBookItem(item, controller));
		}

		if (items != null)
		{
			for (Item item: items)
			{
				PaletteFolder folder = root;
				if (item.getMetadata().containsKey("path"))
				{
					final String[] pathElements = item.getMetadata().get("path").split("/");
					for (final String pathElement : pathElements)
					{
						folder = folder.getFolder(pathElement);
					}
				}
				else if (item.getMetadata().containsKey("source"))
				{
					folder = root.getFolder(item.getMetadata().get("source"));
					if (item.getMetadata().containsKey("trip_name"))
					{
						folder = folder.getFolder(item.getMetadata().get("trip_name"));
					}
					else if (item.getMetadata().containsKey("trip"))
					{
						folder = folder.getFolder(item.getMetadata().get("trip"));
					}
				}
				folder.add(new PalettePlaceBookItem(item, controller));
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
}