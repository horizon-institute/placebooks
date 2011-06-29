package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookEditorPlace;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class PlaceBookCanvas extends FlowPanel
{
	private static final int DEFAULT_COLUMNS = 3;
	private static final int DEFAULT_PAGES = 2;

	private final PlaceBookItemWidgetFactory itemFactory;

	private final Collection<PlaceBookItemWidget> items = new HashSet<PlaceBookItemWidget>();

	private final List<PlaceBookPanel> panels = new ArrayList<PlaceBookPanel>();

	private final boolean panelsVisible;

	private PlaceBook placebook;

	private final PlaceController placeController;

	public PlaceBookCanvas(final PlaceController placeController, final PlaceBookItemWidgetFactory itemFactory,
			final boolean panelsVisible)
	{
		this.placeController = placeController;
		this.itemFactory = itemFactory;
		this.panelsVisible = panelsVisible;

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

	public PlaceBookItemWidget add(final PlaceBookItem item)
	{
		return addToCanvas(item);
	}

	public PlaceBookItemWidgetFactory getItemFactory()
	{
		return itemFactory;
	}

	public Iterable<PlaceBookItemWidget> getItems()
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

	public void remove(final PlaceBookItemWidget item)
	{
		items.remove(item);
		item.removeFromCanvas(this);
		if (item.getPanel() != null)
		{
			item.getPanel().remove(item);
		}
	}

	public void updatePlaceBook(final PlaceBook newPlacebook)
	{
		if (this.placebook != null
				&& (this.placebook.getKey() == null || !this.placebook.getKey().equals(newPlacebook.getKey())))
		{
			placeController.goTo(new PlaceBookEditorPlace(newPlacebook));
		}

		if (this.placebook == null)
		{
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
				addToCanvas(newPlacebook.getItems().get(index));
			}
		}

		this.placebook = newPlacebook;

		for (final PlaceBookItemWidget widget : items)
		{
			if (widget.getItem().getKey() == null)
			{
				final String tempID = widget.getItem().getMetadata("tempID");
				if (tempID != null)
				{
					for (int index = 0; index < newPlacebook.getItems().length(); index++)
					{
						final PlaceBookItem item = newPlacebook.getItems().get(index);
						if (item.hasMetadata("tempID") && item.getMetadata("tempID").equals(tempID))
						{
							widget.setItem(item);
						}
					}
					widget.getItem().removeMetadata("tempID");
				}
			}
		}

		reflow();
	}

	private PlaceBookItemWidget addToCanvas(final PlaceBookItem item)
	{
		final PlaceBookItemWidget itemWidget = itemFactory.createPlaceBookItemWidget(this, item);
		itemWidget.addToCanvas(this);
		itemWidget.setPanel(panels.get(item.getParameter("panel", 0)));
		items.add(itemWidget);
		return itemWidget;
	}
}