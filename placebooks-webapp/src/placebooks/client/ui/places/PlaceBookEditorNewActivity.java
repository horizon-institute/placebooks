package placebooks.client.ui.places;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookEditor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class PlaceBookEditorNewActivity extends PlaceBookActivity
{
	private final PlaceBook placebook;
	private final PlaceController placeController;

	public PlaceBookEditorNewActivity(final PlaceController controller, final PlaceBook placebook, final Shelf shelf)
	{
		super(shelf);
		this.placebook = placebook;
		this.placeController = controller;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final PlaceBookEditor editor = new PlaceBookEditor(placeController, shelf);
		editor.setPlaceBook(placebook);
		panel.setWidget(editor);
	}
}
