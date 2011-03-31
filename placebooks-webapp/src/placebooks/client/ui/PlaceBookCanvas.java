package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookCanvas extends Composite
{
	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookCanvas>
	{
	}

	private static PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	Panel palette;

	private PlaceBook placebook;

	private final Collection<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	public PlaceBookCanvas(final PlaceBook placebook)
	{
		initWidget(uiBinder.createAndBindUi(this));
		setPlaceBook(placebook);
	}

	private void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			final PlaceBookItemFrame frame = new PlaceBookItemFrame(item);
			items.add(frame);
		}
	}
}
