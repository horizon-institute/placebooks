package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import placebooks.client.resources.Resources;
import placebooks.client.ui.items.PlaceBookItemWidget;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPanel extends SimplePanel
{
	protected static final double HEIGHT_PRECISION = 10000;	
	
	private static final Comparator<PlaceBookItemFrame> orderComparator = new Comparator<PlaceBookItemFrame>()
	{
		@Override
		public int compare(final PlaceBookItemFrame o1, final PlaceBookItemFrame o2)
		{
			return o1.getItem().getParameter("order", 0) - o2.getItem().getParameter("order", 0);
		}
	};

	private final int column;

	private final List<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	private final int panelIndex;

	private float panelWidth = 33;

	private final int row;

	public PlaceBookPanel(final int index, final int columns, final boolean visible)
	{
		this.panelIndex = index;
		column = index % columns;
		row = index / columns;
		if (visible)
		{
			setStyleName(Resources.INSTANCE.style().panel());
			if (column == 0)
			{
				addStyleName(Resources.INSTANCE.style().panelleft());
			}
			else if (column == (columns - 1))
			{
				addStyleName(Resources.INSTANCE.style().panelright());
			}
			else
			{
				addStyleName(Resources.INSTANCE.style().panelcenter());
			}
		}
		else
		{
			setStyleName(Resources.INSTANCE.style().panelInvisible());
		}
		setWidth(100f / columns);
	}

	public void add(final PlaceBookItemFrame item)
	{
		final int order = item.getItem().getParameter("order", items.size());
		if (order >= items.size())
		{
			items.add(item);
		}
		else
		{
			items.add(order, item);
		}
	}

	public int getIndex()
	{
		return panelIndex;
	}

	public void reflow()
	{
		Collections.sort(items, orderComparator);

		resize();

		int order = 0;
		int top = getElement().getOffsetTop();
		for (final PlaceBookItemFrame item : items)
		{
			top += layoutItem(item, top);
			order++;
		}
	}

	public void remove(final PlaceBookItemFrame item)
	{
		items.remove(item);
	}

	public void resize()
	{
		final int panelHeight = getElement().getClientWidth() * 2;

		final int panelTop = ((panelHeight + 20) * row);

		getElement().getStyle().setTop(panelTop, Unit.PX);
		setHeight(panelHeight + "px");
	}

	public void setWidth(final float panelWidth)
	{
		this.panelWidth = panelWidth;
		getElement().getStyle().setWidth(panelWidth, Unit.PCT);
		getElement().getStyle().setLeft(column * panelWidth, Unit.PCT);

		resize();
	}

	boolean isIn(final int x, final int y)
	{
		final int left = getElement().getOffsetLeft();
		final int width = getElement().getOffsetWidth();
		final int top = getElement().getOffsetTop() - 20;
		final int height = getElement().getOffsetHeight();
		return left < x && x < (left + width) && top < y && y < (top + height);
	}

	void reflow(final PlaceBookItemWidget newItem, final int inserty, final int height)
	{
		Collections.sort(items, orderComparator);

		resize();

		newItem.getItem().setParameter("panel", panelIndex);

		int order = 0;
		int top = getElement().getOffsetTop();
		boolean inserted = false;
		for (final PlaceBookItemFrame item : items)
		{
			if (!inserted && inserty < top + item.getItemWidget().getOffsetHeight())
			{
				top += height;

				newItem.getItem().setParameter("order", order);

				inserted = true;

				order++;
			}
			top += layoutItem(item, top);

			item.getItem().setParameter("order", order);

			order++;
		}

		if (!inserted)
		{
			newItem.getItem().setParameter("order", order);
		}
	}

	void reflow(final Widget insert, final int inserty, final int height)
	{
		Collections.sort(items, orderComparator);

		resize();

		int top = getElement().getOffsetTop();
		Widget insertTemp = insert;
		for (final PlaceBookItemFrame item : items)
		{
			if (insertTemp != null && inserty < top + item.getItemWidget().getOffsetHeight())
			{
				top += layoutInsert(insertTemp, top, height);

				insertTemp = null;
			}
			top += layoutItem(item, top);
		}

		if (insertTemp != null && inserty > top)
		{
			layoutInsert(insertTemp, top, height);
		}
	}

	private int layoutInsert(final Widget insert, final int top, final int height)
	{
		insert.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		insert.getElement().getStyle().setTop(top, Unit.PX);
		insert.getElement().getStyle().setLeft(column * panelWidth, Unit.PCT);

		insert.setWidth(panelWidth + "%");
		insert.setHeight(height + "px");

		return insert.getOffsetHeight();
	}

	private int layoutItem(final PlaceBookItemFrame item, final int top)
	{
		String heightString;
		
		if (item.getItem().hasParameter("height") && item.getPanel() != null)
		{
			final int height = item.getItem().getParameter("height");
			final double heightPCT = height / HEIGHT_PRECISION;
			final int heightPX = (int) (item.getPanel().getOffsetHeight() * heightPCT);

			heightString = heightPX + "px";
		}
		else
		{
			heightString = "";
		}

		item.resize(column * panelWidth + "%", top + "px", panelWidth + "%", heightString);
		
		return item.getOffsetHeight();
	}
}