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

	private int pageIndex;

	private int columnOffset;

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<PlaceBookColumn> columns = new ArrayList<PlaceBookColumn>();

	private PlaceBook placebook;
	// TODO PlaceBookPage page;

	@UiField
	Label pageNumber;

	@UiField
	Panel columnPanel;

	public PlaceBookPage()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void add(final PlaceBookItemFrame item)
	{
		addImpl(item);
		placebook.add(item.getItem());
		item.getItemWidget().setPlaceBook(placebook);
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

	public void reflow()
	{
		final double panelHeight = getOffsetWidth() * 2 / 3;
		// setHeight(panelHeight + "px");

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

	public void setPage(final PlaceBook newPlaceBook, final int pageIndex, final PlaceBookItemFrameFactory factory,
			final int columnCount)
	{
		assert placebook == null;
		this.placebook = newPlaceBook;

		this.pageIndex = pageIndex;
		pageNumber.setText("" + (pageIndex + 1));

		double left = 0;
		for (int index = 0; index < columnCount; index++)
		{
			final double widthPCT = 100 / columnCount;
			final int panelIndex = (pageIndex * columnCount) + index;
			final PlaceBookColumn panel = new PlaceBookColumn(panelIndex, columnCount, left, widthPCT,
					factory.isEditable());
			columns.add(panel);
			columnPanel.add(panel);

			left += widthPCT;
		}

		columnOffset = pageIndex * columnCount;

		for (final PlaceBookItem item : newPlaceBook.getItems())
		{
			final int panelIndex = item.getParameter("panel");
			if (panelIndex >= columnOffset && (panelIndex < columnCount + columnOffset))
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

		refreshItemPlaceBook();
		reflow();
	}

	private void addImpl(final PlaceBookItemFrame item)
	{
		if (item == null) { return; }
		items.add(item);
		item.setPanel(columns.get(item.getItem().getParameter("panel", 0) - columnOffset));
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
}