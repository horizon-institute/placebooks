package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.UIMessages;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PlaceBookList extends CellList<PlaceBookEntry>
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	interface Bundle extends ClientBundle
	{
		@Source("PlaceBookList.css")
		Style style();
	}

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

			sb.appendHtmlConstant("<div class=\"" + STYLES.style().listItem() + "\">");

			// Add the name and address.
			sb.appendHtmlConstant("<div>");
			if (value.getKey().equals("new"))
			{
				sb.appendEscaped(value.getTitle());
			}
			else
			{
				sb.appendEscaped(uiMessages.editPlaceBook(value.getTitle()));
			}
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("<div><small style=\"color: #333;\">");
			if (value.getDescription() == null)
			{
				sb.appendEscaped(uiMessages.noDesc());
			}
			else
			{
				sb.appendEscaped(value.getDescription());
			}

			sb.appendHtmlConstant("</small></div>");
		}
	}

	interface Style extends CssResource
	{
		String listItem();
	}

	private static final Bundle STYLES = GWT.create(Bundle.class);

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
		newPlaceBook = PlaceBookService
				.parse(	PlaceBookEntry.class,
						"{\"key\": \"new\", \"title\": \"" + uiMessages.createNewPlaceBook() + "\", \"description\": \"" + uiMessages.createNewPlaceBookDesc() + "\"}");
	}

	public PlaceBookList()
	{
		super(new PlaceBookCell(), placebookKeyProvider);
		STYLES.style().ensureInjected();
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

	@SuppressWarnings("unchecked")
	public void setShelf(final Shelf shelf)
	{
		if (shelf == null)
		{
			setRowData(Collections.EMPTY_LIST);
		}
		else
		{
			final List<PlaceBookEntry> entries = new ArrayList<PlaceBookEntry>();
			entries.add(newPlaceBook);
			for (final PlaceBookEntry entry : entries)
			{
				entries.add(entry);
			}
			setRowData(entries);
		}
	}
}