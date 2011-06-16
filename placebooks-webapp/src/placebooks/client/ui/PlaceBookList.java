package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PlaceBookList extends CellList<PlaceBookEntry>
{
	static class PlaceBookCell extends AbstractCell<PlaceBookEntry>
	{
		public PlaceBookCell()
		{
		}

		@Override
		public void render(final Context context, final PlaceBookEntry value, final SafeHtmlBuilder sb)
		{
			// Value can be null, so do a null check..
			if (value == null) { return; }

			sb.appendHtmlConstant("<div style=\"" + placebooks.client.resources.Resources.INSTANCE.style().listItem()
					+ "\">");

			// Add the name and address.
			sb.appendHtmlConstant("<div>");
			if (value.getTitle() == null)
			{
				sb.appendEscaped("No Title");
			}
			else
			{
				sb.appendEscaped(value.getTitle());
			}
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("<div>");
			if (value.getDescription() == null)
			{
				sb.appendEscaped("No Description");
			}
			else
			{
				sb.appendEscaped(value.getDescription());
			}

			sb.appendHtmlConstant("</div>");
		}
	}

	private static final PlaceBookEntry newPlaceBook;

	private static final ProvidesKey<PlaceBookEntry> placebookKeyProvider = new ProvidesKey<PlaceBookEntry>()
	{
		@Override
		public Object getKey(final PlaceBookEntry entry)
		{
			return entry.getKey();
		}
	};

	static
	{
		newPlaceBook = PlaceBookEntry
				.parse("{\"key\": \"new\", \"title\": \"New PlaceBook\", \"description\": \"Start a new placebook\"}");
	}

	public PlaceBookList()
	{
		super(new PlaceBookCell(), placebookKeyProvider);
		// cellList.setPageSize(30);
		// cellList.setKeyboardPagingPolicy(KeyboardPagingPolicy.INCREASE_RANGE);
		setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
	}

	public void addSelectionHandler(final SelectionChangeEvent.Handler handler)
	{
		final SingleSelectionModel<PlaceBookEntry> selectionModel = new SingleSelectionModel<PlaceBookEntry>(
				placebookKeyProvider);
		setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(handler);
	}

	@SuppressWarnings("unchecked")
	public PlaceBookEntry getSelection()
	{
		return ((SingleSelectionModel<PlaceBookEntry>) getSelectionModel()).getSelectedObject();
	}

	public void setShelf(final Shelf shelf)
	{
		newPlaceBook.setOwner(shelf.getUser());
		final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
		entries.add(newPlaceBook);
		for (int index = 0; index < shelf.getEntries().length(); index++)
		{
			entries.add(shelf.getEntries().get(index));
		}
		setRowData(entries);
	}

}
