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

public class PlaceBookPage extends FlowPanel
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
	
	private static final int A4Length = 297;
	//private static final int A4Width = 210;
	private static final int Margin = 20;
	private int pageIndex;

	private static final int DEFAULT_COLUMNS = 3;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PlaceBookColumn> columns = new ArrayList<PlaceBookColumn>();

	private PlaceBook placebook;
	// TODO PlaceBookPage page;

	public PlaceBookPage()
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
		item.setPanel(columns.get(item.getItem().getParameter("panel", 0)));
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

	public Iterable<PlaceBookColumn> getColumns()
	{
		return columns;
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}

	public void reflow()
	{
		final double panelHeight = getOffsetWidth() * 2 / 3;
		setHeight(panelHeight + "px");

		for (final PlaceBookColumn column : columns)
		{
			column.reflow();
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
		items.remove(item);
		item.setPanel(null);
		placebook.remove(item.getItem());
		refreshItemPlaceBook();
	}

	public void setPage(final PlaceBook newPlaceBook, final int pageIndex, final PlaceBookItemFrameFactory factory,
			final boolean panelsVisible)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;
		clear();
		
		this.pageIndex = pageIndex;
		
		int columnCount = DEFAULT_COLUMNS;
		try
		{
			columnCount = Integer.parseInt(newPlaceBook.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}

		final double usableWidth = A4Length - (2 * Margin);
		final double panelWidth = A4Length / columnCount;
		final double shortPanelWidth = panelWidth - Margin;

		double left = 0;
		for (int index = 0; index < columnCount; index++)
		{
			double widthPCT = panelWidth / usableWidth * 100;
			if (index == 0 || index == columnCount - 1)
			{
				widthPCT = shortPanelWidth / usableWidth * 100;
			}
			final int panelIndex = (pageIndex * columnCount) + index;
			final PlaceBookColumn panel = new PlaceBookColumn(panelIndex, columnCount, left, widthPCT, panelsVisible);
			columns.add(panel);

			left += widthPCT;
		}

		for (final PlaceBookItem item : newPlaceBook.getItems())
		{
			int page = item.getParameter("panel") / columnCount;
			if(page == pageIndex)
			{
				addImpl(factory.createFrame(item));
			}
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