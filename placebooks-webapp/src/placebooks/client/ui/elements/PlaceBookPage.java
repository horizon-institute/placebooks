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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPage extends Composite
{
	interface PlaceBookPageUiBinder extends UiBinder<Widget, PlaceBookPage>
	{
	}

	private static PlaceBookPageUiBinder uiBinder = GWT.create(PlaceBookPageUiBinder.class);

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PlaceBookColumn> columns = new ArrayList<PlaceBookColumn>();

	private PlaceBook placebook;
	// TODO PlaceBookPage page;

	private int index;
	
	@UiField
	Label pageNumber;

	@UiField
	Panel columnPanel;

	void clearFlip()
	{
		getElement().getStyle().clearWidth();
		pageNumber.getElement().getStyle().clearWidth();
		columnPanel.getElement().getStyle().clearWidth();
	}
	
	void setFlip(double flipX, double pageWidth)
	{
		setWidth(flipX + "px");
		pageNumber.setWidth(pageWidth - 30 + "px");
		columnPanel.setWidth(pageWidth - 60 + "px");
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public PlaceBookPage(final PlaceBook page, final int pageIndex, int defaultColumnCount, final PlaceBookItemFrameFactory factory)
	{
		initWidget(uiBinder.createAndBindUi(this));
		setPage(page, pageIndex, defaultColumnCount, factory);
	}

	public Iterable<PlaceBookColumn> getColumns()
	{
		return columns;
	}

	public Iterable<PlaceBookItemFrame> getItems()
	{
		return items;
	}

	public PlaceBook getPlaceBook()
	{
		return placebook;
	}
	
	public void setSize(final double width, final double height)
	{
//		columnPanel.setHeight(height - 60 + "px");
//		columnPanel.setWidth(width - 60 + "px");
		
		reflow();
	}

	public void reflow()
	{
		for (final PlaceBookColumn column : columns)
		{
			column.reflow();
		}
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		item.setPanel(null);
		placebook.remove(item.getItem());
		refreshItemPlaceBook();
	}

	private void setPage(final PlaceBook newPlaceBook, final int pageIndex, final int defaultColumnCount, final PlaceBookItemFrameFactory factory)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;
		setIndex(pageIndex);

		int columnCount = defaultColumnCount;
		try
		{
			columnCount = Integer.parseInt(newPlaceBook.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}
		
		double left = 0;
		for (int index = 0; index < columnCount; index++)
		{
			final double widthPCT = 100 / columnCount;
			final PlaceBookColumn panel = new PlaceBookColumn(newPlaceBook, index, columnCount, left, widthPCT,

					factory.isEditable());
			columns.add(panel);
			columnPanel.add(panel);

			left += widthPCT;
		}

		for (final PlaceBookItem item : newPlaceBook.getItems())
		{
			add(factory.createFrame(item));
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

		refreshItemPlaceBook();
		reflow();
	}

	private void add(final PlaceBookItemFrame item)
	{
		if (item == null) { return; }
		items.add(item);
		int columnIndex = item.getItem().getParameter("panel", 0);
		if(columnIndex >= columns.size())
		{
			columnIndex = columnIndex % columns.size();
			item.getItem().setParameter("panel", columnIndex);
		}
		item.setPanel(columns.get(columnIndex));
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

	private final void refreshItemPlaceBook()
	{
		for (final PlaceBookItemFrame item : items)
		{
			item.getItemWidget().setPlaceBook(placebook);
		}
	}

	public void setIndex(int index)
	{
		this.index = index;
		pageNumber.setText("" + (index + 1));
	}
}
