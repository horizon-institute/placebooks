package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.items.frames.PlaceBookItemFrameFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class PlaceBookCanvas extends FlowPanel
{
	interface Style extends CssResource
	{
		String canvas();
	    String page();
	    String pageInvisible();
	}

	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookCanvas.css")
		Style style();
	}
	
	private static final Bundle STYLES = GWT.create(Bundle.class);
	
	public static final int A4Length = 297;
	public static final int A4Width = 210;
	public static final int Margin = 20;

	private static final int DEFAULT_COLUMNS = 3;
	private static final int DEFAULT_PAGES = 2;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();
	private final List<Panel> pages = new ArrayList<Panel>();

	private PlaceBook placebook;

	public PlaceBookCanvas()
	{
		STYLES.style().ensureInjected();
		setStyleName(STYLES.style().canvas());

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
		item.getItemWidget().setPlaceBook(placebook);
	}

	private void addImpl(final PlaceBookItemFrame item)
	{
		if (item == null) { return; }
		items.add(item);
		item.setPanel(panels.get(item.getItem().getParameter("panel", 0)));
	}

	private PlaceBookItemFrame getFrame(final PlaceBookItem item)
	{
		for (final PlaceBookItemFrame frame : items)
		{
			if (frame.getItem().getKey() != null)
			{
				if (frame.getItem().getKey().equals(item.getKey())) { return frame; }
			}
			else if (item.hasMetadata("tempID")
					&& item.getMetadata("tempID").equals(frame.getItem().getMetadata("tempID"))) { return frame; }
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
		for (final Panel page : pages)
		{
			final double panelHeight = page.getOffsetWidth() * 2 / 3;
			page.setHeight(panelHeight + "px");
		}

		for (final PlaceBookPanel panel : panels)
		{
			panel.reflow();
		}
	}

	private final void refreshItemPlaceBook()
	{
		for (final PlaceBookItemFrame item : items)
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

	public void setPlaceBook(final PlaceBook newPlaceBook, final PlaceBookItemFrameFactory factory,
			final boolean panelsVisible)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;
		clear();
		int pageCount = DEFAULT_PAGES;
		try
		{
			pageCount = Integer.parseInt(newPlaceBook.getMetadata("pageCount"));
		}
		catch (final Exception e)
		{
		}

		final int columns = DEFAULT_COLUMNS;
		try
		{
			pageCount = Integer.parseInt(newPlaceBook.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}

		final double usableWidth = A4Length - (2 * Margin);
		final double panelWidth = A4Length / columns;
		final double shortPanelWidth = panelWidth - Margin;
		for (int pageIndex = 0; pageIndex < pageCount; pageIndex++)
		{
			final FlowPanel page = new FlowPanel();
			if (panelsVisible)
			{
				page.setStyleName(STYLES.style().page());
				// final double padding = 2000 / A4Length;
				// page.getElement().getStyle().setPadding(padding, Unit.PCT);
			}
			else
			{
				page.setStyleName(STYLES.style().pageInvisible());
			}

			double left = 0;
			for (int index = 0; index < columns; index++)
			{
				double widthPCT = panelWidth / usableWidth * 100;
				if (index == 0 || index == columns - 1)
				{
					widthPCT = shortPanelWidth / usableWidth * 100;
				}
				final int panelIndex = (pageIndex * columns) + index;
				final PlaceBookPanel panel = new PlaceBookPanel(panelIndex, columns, left, widthPCT, panelsVisible);
				panels.add(panel);
				page.add(panel);

				left += widthPCT;
			}

			add(page);
			pages.add(page);
		}

		for (final PlaceBookItem item : newPlaceBook.getItems())
		{
			addImpl(factory.createFrame(item));
		}

		refreshItemPlaceBook();
		reflow();
	}

	public void updatePlaceBook(final PlaceBook newPlaceBook)
	{
		this.placebook = newPlaceBook;

		for (final PlaceBookItem item : newPlaceBook.getItems())
		{
			final PlaceBookItemFrame frame = getFrame(item);
			if (frame != null)
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