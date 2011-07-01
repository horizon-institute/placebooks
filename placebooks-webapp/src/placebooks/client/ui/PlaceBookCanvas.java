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
				reflow();				
			}
		});
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
	
	public void add(final PlaceBookItemFrame item)
	{
		items.add(item);
		super.add(item);
		item.setPanel(panels.get(item.getItem().getParameter("panel", 0)));
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		super.remove(item);
		if (item.getPanel() != null)
		{
			item.getPanel().remove(item);
		}
	}

	private PlaceBookItemFrame getFrame(final PlaceBookItem item)
	{
		for(PlaceBookItemFrame frame: items)
		{
			if(frame.getItem().getKey() != null && frame.getItem().getKey().equals(item.getKey()))
			{
				return frame;
			}
			
			if (item.hasMetadata("tempID") && item.getMetadata("tempID").equals(frame.getItem().getMetadata("tempID")))
			{
				return frame;
			}			
		}
		return null;
	}
	
	public void setPlaceBook(final PlaceBook newPlacebook, final PlaceBookItemFrameFactory factory, boolean panelsVisible)
	{
		assert placebook == null;
		this.placebook = newPlacebook;		
		clear();
		int pages = DEFAULT_PAGES;
		try
		{
			pages = Integer.parseInt(newPlacebook.getMetadata("pageCount"));
		}
		catch (final Exception e)
		{
		}

		final int columns = DEFAULT_COLUMNS;
		try
		{
			pages = Integer.parseInt(newPlacebook.getMetadata("columns"));
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

		for (int index = 0; index < newPlacebook.getItems().length(); index++)
		{
			add(factory.createFrame(newPlacebook.getItems().get(index)));
		}
	}
	
	public void updatePlaceBook(final PlaceBook newPlacebook)
	{
		this.placebook = newPlacebook;

		for (int index = 0; index < newPlacebook.getItems().length(); index++)
		{
			final PlaceBookItem item = newPlacebook.getItems().get(index);
			final PlaceBookItemFrame frame = getFrame(item);
			if(frame != null)
			{
				frame.getItemWidget().update(item);
			}
		}

		reflow();
	}

//	private PlaceBookItemFrame addToCanvas(final PlaceBookItem item)
//	{
//		final PlaceBookItemFrame itemFrame = factory.createFrame(); //PlaceBookItemWidget(this, item);
//		itemFrame.setItemWidget(PlaceBookItemWidgetFactory.createItemWidget(item, editable));
//		//itemWidget.addToCanvas(this);
//		//itemWidget.setPanel(panels.get(item.getParameter("panel", 0)));
//		items.add(itemFrame);
//		add(itemFrame);
//		return itemFrame;
//	}
}