package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

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

	private PlaceBook page;

	private int index;

	@UiField
	Label pageNumber;

	@UiField
	Panel columnPanel;

	public PlaceBookPage(final PlaceBook page, final PlaceBookController controller, final int pageIndex,
			final int defaultColumnCount)
	{
		this.page = page;
		initWidget(uiBinder.createAndBindUi(this));

		setIndex(pageIndex);

		int columnCount = defaultColumnCount;
		try
		{
			columnCount = Integer.parseInt(page.getMetadata("columns"));
		}
		catch (final Exception e)
		{
		}

		double left = 0;
		for (int index = 0; index < columnCount; index++)
		{
			final double widthPCT = 100 / columnCount;
			final PlaceBookColumn panel = new PlaceBookColumn(this, index, columnCount, left, widthPCT,
					controller.canEdit());
			columns.add(panel);
			columnPanel.add(panel);

			left += widthPCT;
		}

		for (final PlaceBookItem item : page.getItems())
		{
			add(controller.createFrame(item));
		}

		reflow();
	}

	public Iterable<PlaceBookColumn> getColumns()
	{
		return columns;
	}

	public int getIndex()
	{
		return index;
	}

	public Iterable<PlaceBookItemFrame> getItems()
	{
		return items;
	}

	public PlaceBook getPlaceBook()
	{
		return page;
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
		item.setColumn(null);
		page.remove(item.getItem());
	}

	public void setIndex(final int index)
	{
		this.index = index;
		pageNumber.setText("" + (index + 1));
	}

	public void setSize(final double width, final double height)
	{
		// columnPanel.setHeight(height - 60 + "px");
		// columnPanel.setWidth(width - 60 + "px");

		reflow();
	}

	public void update(final PlaceBook newPage)
	{
		this.page = newPage;
		page.removeMetadata("tempID");

		for (final PlaceBookItem item : newPage.getItems())
		{
			final PlaceBookItemFrame frame = getFrame(item);
			if (frame != null)
			{
				frame.getItemWidget().update(item);
			}
			else
			{
				GWT.log("Item not found!");
			}
		}

		page.clearItems();
		for (final PlaceBookItemFrame frame : items)
		{
			page.add(frame.getItem());
		}

		reflow();
	}

	void add(final PlaceBookItemFrame item)
	{
		if (item == null) { return; }
		items.add(item);
		int columnIndex = item.getItem().getParameter("column", 0);
		if (columnIndex >= columns.size())
		{
			columnIndex = columnIndex % columns.size();
			item.getItem().setParameter("column", columnIndex);
		}
		item.setColumn(columns.get(columnIndex));
	}

	void clearFlip()
	{
		getElement().getStyle().clearWidth();
		pageNumber.getElement().getStyle().clearWidth();
		columnPanel.getElement().getStyle().clearWidth();
	}

	void setFlip(final double flipX, final double pageWidth)
	{
		setWidth(flipX + "px");
		pageNumber.setWidth(pageWidth - 30 + "px");
		columnPanel.setWidth(pageWidth - 60 + "px");
	}

	// private final void refreshItemPlaceBook()
	// {
	// for (final PlaceBookItemFrame item : items)
	// {
	// item.getItemWidget().setPlaceBook(page);
	// }
	// }

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
}
