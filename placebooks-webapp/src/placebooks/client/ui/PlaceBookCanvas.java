package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookCanvas extends FlowPanel
{
	private static final int DEFAULT_COLUMNS = 3;
	private static final int DEFAULT_PAGES = 2;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();	

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	private PlaceBook placebook;

	public PlaceBookCanvas()
	{
		setStyleName(Resources.INSTANCE.style().canvas());

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(final ResizeEvent event)
			{
				reflow();				
			}
		});
	}

	public void add(final PlaceBookItemFrame item)
	{
		addImpl(item);
		placebook.add(item.getItem());
		refreshItemPlaceBook();
	}

	private void addImpl(final PlaceBookItemFrame item)
	{
		items.add(item);
		item.setPanel(panels.get(item.getItem().getParameter("panel", 0)));		
	}

	private PlaceBookItemFrame getFrame(final PlaceBookItem item)
	{
		for(PlaceBookItemFrame frame: items)
		{
			if(frame.getItem().getKey() != null)
			{
				if(frame.getItem().getKey().equals(item.getKey()))
				{	
					return frame;
				}
			}
			else if (item.hasMetadata("tempID") && item.getMetadata("tempID").equals(frame.getItem().getMetadata("tempID")))
			{
				return frame;
			}			
		}
		return null;
	}

	public Iterable<PlaceBookItemFrame> getItems()
	{
		return items;
	}
	
	public Iterable<PlaceBookPanel> getPanels()
	{
		return panels;
	}
	
	public PlaceBook getPlaceBook()
	{
		return placebook;
	}
	
	public void reflow()
	{
		for (final PlaceBookPanel panel : panels)
		{
			panel.reflow();
		}
	}

	private final void refreshItemPlaceBook()
	{
		for(PlaceBookItemFrame item: items)
		{
			item.getItemWidget().setPlaceBook(placebook);
		}	
	}
	
	public void remove(final PlaceBookItemFrame item)
	{
		removeImpl(item);
		placebook.remove(item.getItem());
		refreshItemPlaceBook();		
	}

	private void removeImpl(final PlaceBookItemFrame item)
	{
		items.remove(item);
		item.setPanel(null);
	}
	
	public void setPlaceBook(final PlaceBook newPlaceBook, final PlaceBookItemFrameFactory factory, boolean panelsVisible)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;	
		clear();
		int pages = DEFAULT_PAGES;
		try
		{
			pages = Integer.parseInt(newPlaceBook.getMetadata("pageCount"));
		}
		catch (final Exception e)
		{
		}

		final int columns = DEFAULT_COLUMNS;
		try
		{
			pages = Integer.parseInt(newPlaceBook.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}

		for (int index = 0; index < (pages * columns); index++)
		{
			final PlaceBookPanel panel = new PlaceBookPanel(index, columns, panelsVisible);
			panels.add(panel);
			add(panel);
		}

		for (PlaceBookItem item: newPlaceBook.getItems())
		{
			addImpl(factory.createFrame(item));		
		}

		refreshItemPlaceBook();	
		reflow();		
	}
	
	public void updatePlaceBook(final PlaceBook newPlaceBook)
	{
		this.placebook = newPlaceBook;

		for (PlaceBookItem item: newPlaceBook.getItems())
		{
			final PlaceBookItemFrame frame = getFrame(item);
			if(frame != null)
			{
				frame.getItemWidget().update(item);
			}
		}
		
		placebook.clearItems();
		for (final PlaceBookItemFrame item : items)
		{
			placebook.add(item.getItem());
		}

		refreshItemPlaceBook();		
		reflow();
	}
}