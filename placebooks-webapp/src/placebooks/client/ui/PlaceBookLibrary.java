package placebooks.client.ui;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLibrary extends Composite
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookLibrary>
	{
	}

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	Panel placebooks;

	@UiField
	Label titleLabel;

	@UiField
	PlaceBookToolbar toolbar;

	private Shelf shelf;

	public PlaceBookLibrary(final PlaceController placeController, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("PlaceBooks Library");
		titleLabel.setText("Library");

		toolbar.setPlaceController(placeController);
		toolbar.setShelf(shelf);
		toolbar.setShelfListener(new PlaceBookToolbarLogin.ShelfListener()
		{
			@Override
			public void shelfChanged(final Shelf shelf)
			{
				setShelf(shelf);
			}
		});
		setShelf(shelf);

		RootPanel.get().getElement().getStyle().clearOverflow();
	}

	private void setShelf(final Shelf shelf)
	{
		if (this.shelf != shelf)
		{
			this.shelf = shelf;
			placebooks.clear();
			if (shelf != null)
			{
				int index = 0;
				for (final PlaceBookEntry entry : shelf.getEntries())
				{
					PlaceBookEntryWidget widget = new PlaceBookEntryWidget(toolbar, entry);
					if(index % 5 == 0)
					{
						widget.getElement().getStyle().setProperty("clear", "left");
					}
					index++;
					placebooks.add(widget);
				}
			}
		}
	}
}
