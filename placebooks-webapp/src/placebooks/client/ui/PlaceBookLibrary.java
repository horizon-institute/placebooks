package placebooks.client.ui;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLibrary extends Composite
{
	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookLibrary>
	{
	}

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	PlaceBookToolbar toolbar;
	
	@UiField
	Panel placebooks;
	
	public PlaceBookLibrary(PlaceController placeController, Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("PlaceBooks - Your Library");
		
		toolbar.setPlaceController(placeController);
		toolbar.setShelf(shelf);		
		toolbar.setShelfListener(new PlaceBookToolbarLogin.ShelfListener()
		{
			@Override
			public void shelfChanged(Shelf shelf)
			{		
				setShelf(shelf);
			}
		});
		GWT.log("Library " + shelf);
		setShelf(shelf);
	}	

	private void setShelf(Shelf shelf)
	{
		placebooks.clear();
		for(PlaceBookEntry entry: shelf.getEntries())
		{
			placebooks.add(new PlaceBookEntryWidget(toolbar.getPlaceController(), entry));
		}	
	}
}
