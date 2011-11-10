package placebooks.client.ui;

import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.PlaceBookShelf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookLibrary extends PlaceBookPlace
{
	@Prefix("browse")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookLibrary>
	{
		@Override
		public PlaceBookLibrary getPlace(final String token)
		{
			return new PlaceBookLibrary();
		}

		@Override
		public String getToken(final PlaceBookLibrary place)
		{
			return "library";
		}
	}

	interface PlaceBookLibraryUiBinder extends UiBinder<Widget, PlaceBookLibrary>
	{
	}

	private static PlaceBookLibraryUiBinder uiBinder = GWT.create(PlaceBookLibraryUiBinder.class);

	@UiField
	PlaceBookShelf shelf;

	public PlaceBookLibrary()
	{
		super(null);
	}

	public PlaceBookLibrary(final Shelf shelf)
	{
		super(shelf);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget library = uiBinder.createAndBindUi(this);

		Window.setTitle("PlaceBooks Library");

		toolbar.setPlace(this);

		panel.setWidget(library);
		
		shelfUpdated();		
	}

	@Override
	protected void shelfUpdated()
	{
		shelf.setShelf(this, getShelf(), true);
	}
}