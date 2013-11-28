package org.placebooks.client.controllers;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.Resources;
import org.placebooks.client.model.Item;
import org.placebooks.client.model.Item.Type;
import org.placebooks.client.model.Page;
import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.ui.images.markers.Markers;
import org.wornchaos.client.controllers.DelegateController;
import org.wornchaos.client.controllers.SomethingController;
import org.wornchaos.logger.Log;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;

public class ItemController extends DelegateController<Item>
{
	private final boolean canEdit;
	private Item item;

	public ItemController(final Item item, final SomethingController<?> controller)
	{
		super(controller);
		canEdit = false;
		setItem(item);
	}

	public ItemController(final Item item, final SomethingController<?> controller, final boolean canEdit)
	{
		super(controller);
		this.canEdit = canEdit;
		setItem(item);
	}

	public boolean canEdit()
	{
		return canEdit;
	}

	public PlaceBook getPlaceBook()
	{
		if (controller instanceof PlaceBookController) { return ((PlaceBookController) controller).getItem(); }
		return null;
	}

	public void gotoPage(final int page)
	{
		// TODO!
	}
	
	public void removeParameter(final String name)
	{
		if(getItem().getParameters().containsKey(name))
		{
			Log.info("Removed " + name + " parameter");
			getItem().getParameters().remove(name);
			markChanged();
		}
	}
	
	public static ImageResource getIcon(Item item)
	{
		if(item == null || item.getType() == null)
		{
			return Resources.IMAGES.pallette_text();
		}
		switch(item.getType())
		{
			case TextItem:
				return Resources.IMAGES.pallette_text();				
				
			case ImageItem:
				return Resources.IMAGES.pallette_image();
				
			case VideoItem:
				return Resources.IMAGES.pallette_video();
				
			case AudioItem:
				return Resources.IMAGES.pallette_audio();
						
			case GPSTraceItem:
				return Resources.IMAGES.pallette_map();
				
			case WebBundleItem:
				return Resources.IMAGES.pallette_web();
				
				default:
					return null;
		}
	}
	
	public static String getURL(Item item, Type type)
	{
		Type itemType = type;
		if(type == null)
		{
			itemType = item.getType();
		}
		
		if(itemType == null)
		{
			itemType = Item.Type.ImageItem;
		}
		
		if (item.getHash() != null) { return PlaceBooks.getServer().getHostURL() + "command/media?hash=" + item.getHash() + "&type=" + itemType.name(); }

		return null;
	}
	
	public static String getThumbURL(Item item)
	{
		if (item.isMedia())
		{
			if (item.getHash() != null) { return PlaceBooks.getServer().getHostURL()
					+ "command/media?type=thumb&hash=" + item.getHash(); }
		}

		return getURL(item, null);
	}


	
	public static ImageResource getMarkerImage(Item item)
	{
		final int markerID = item.getParameter("marker", 0);

		if (markerID == 0)
		{
			return Markers.IMAGES.marker();
		}
		else
		{
			final char markerPostFix = (char) markerID;
			final ResourcePrototype result = Markers.IMAGES.getResource("marker" + markerPostFix);
			if (result instanceof ImageResource) { return (ImageResource) result; }
			return Markers.IMAGES.marker();
		}
	}
	
	
	public void setParameter(final String name, final int value)
	{
		if(getItem().getParameters().containsKey(name))
		{
			int oldValue = getItem().getParameters().get(name);
			if(oldValue != value)
			{
				Log.info("Set " + name + " parameter to " + value);				
				markChanged();
			}
		}
		else
		{
			Log.info("Set " + name + " parameter to " + value);			
			markChanged();			
		}
		getItem().getParameters().put(name, value);
	}

	public void gotoPage(final Page page)
	{
		// TODO!
	}

	public void setItem(final Item item)
	{
		if(item != null && item.getId() != null)
		{
			item.getMetadata().remove("tempID");
		}
		
		this.item = item;		
	}

	@Override
	public Item getItem()
	{
		return item;
	}
}