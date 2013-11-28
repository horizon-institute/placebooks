package org.placebooks.client.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.placebooks.client.model.Item;
import org.placebooks.client.model.Page;
import org.placebooks.client.ui.items.frames.PlaceBookItemFrame;
import org.wornchaos.client.ui.CompositeView;
import org.wornchaos.logger.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PageView extends CompositeView<Page>
{
	interface PlaceBookPageUiBinder extends UiBinder<Widget, PageView>
	{
	}

	private static PlaceBookPageUiBinder uiBinder = GWT.create(PlaceBookPageUiBinder.class);

	private final Collection<PlaceBookItemFrame> items = new HashSet<PlaceBookItemFrame>();

	private final List<ColumnView> columns = new ArrayList<ColumnView>();

	private Page page;

	private int index;

	@UiField
	Label pageNumber;

	@UiField
	Panel columnPanel;

	public PageView(final Page page, final DragController controller, final int pageIndex,
			final int defaultColumnCount)
	{
		this.page = page;
		initWidget(uiBinder.createAndBindUi(this));

		setIndex(pageIndex);

		int columnCount = defaultColumnCount;
		try
		{
			columnCount = Integer.parseInt(page.getMetadata().get("columns"));
		}
		catch (final Exception e)
		{
		}

		double left = 0;
		for (int index = 0; index < columnCount; index++)
		{
			final double widthPCT = 100 / columnCount;
			final ColumnView panel = new ColumnView(this, index, columnCount, left, widthPCT,
					controller.canEdit());
			columns.add(panel);
			columnPanel.add(panel);

			left += widthPCT;
		}
		
		if(page.getId() == null)
		{
			page.getMetadata().put("tempID", ""+System.currentTimeMillis());
		}

		for (final Item item : page.getItems())
		{
			add(controller.createFrame(item));
		}

		reflow();
	}

	public Iterable<ColumnView> getColumns()
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

	public Page getPage()
	{
		return page;
	}

	public void reflow()
	{
		for (final ColumnView column : columns)
		{
			column.reflow();
		}
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
		item.setColumn(null);
		page.getItems().remove(item.getItem());
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

	public void itemChanged(final Page newPage)
	{
		page = newPage;
		if(page.getId() != null)
		{
			page.getMetadata().remove("tempID");
		}

		for (final Item item : newPage.getItems())
		{
			final PlaceBookItemFrame frame = getFrame(item);
			if (frame != null)
			{
				frame.getItemWidget().getController().setItem(item);
			}
			else
			{
				Log.error("Item not found:" + item.getType() +"."+ item.getId() + " - " + item.getMetadata().get("tempID"));
			}
		}

		page.getItems().clear();
		for (final PlaceBookItemFrame frame : items)
		{
			page.getItems().add(frame.getItem());
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
			item.getItem().getParameters().put("column", columnIndex);
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

	private PlaceBookItemFrame getFrame(final Item item)
	{
		for (final PlaceBookItemFrame frame : items)
		{
			if (frame.getItem().getId() != null)
			{
				if (frame.getItem().getId().equals(item.getId())) { return frame; }
			}
			else if (item.getMetadata().containsKey("tempID")
					&& item.getMetadata().get("tempID").equals(frame.getItem().getMetadata().get("tempID"))) { return frame; }
		}
		return null;
	}
}
