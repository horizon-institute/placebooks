package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookEditor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookEditorNewPlace extends PlaceBookPlace
{
	@Prefix("create")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditorNewPlace>
	{
		@Override
		public PlaceBookEditorNewPlace getPlace(final String token)
		{
			return new PlaceBookEditorNewPlace(null);
		}

		@Override
		public String getToken(final PlaceBookEditorNewPlace place)
		{
			return "placebook";
		}
	}

	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";

	public PlaceBookEditorNewPlace(final Shelf shelf)
	{
		super(shelf);
	}

	public PlaceBook getPlaceBook()
	{
		final PlaceBook placebook = PlaceBook.parse(newPlaceBook);
		if (getShelf() != null)
		{
			placebook.setOwner(getShelf().getUser());
		}
		return placebook;
	}
	
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookEditor editor = new PlaceBookEditor(getPlaceController(), getShelf());
		editor.setPlaceBook(getPlaceBook());
		panel.setWidget(editor);
	}	
}