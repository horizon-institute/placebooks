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
	private static PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookCanvas>
	{
	}

	@UiField
	Panel palette;

	private PlaceBook placebook;
	
	private final Collection<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();
	
	public PlaceBookCanvas(PlaceBook placebook)
	{
		initWidget(uiBinder.createAndBindUi(this));
		setPlaceBook(placebook);
	}
	
	private void setPlaceBook(PlaceBook placebook)
	{
		this.placebook = placebook;
		
		for(PlaceBookItem item: placebook.getItems())
		{
			PlaceBookItemFrame frame = new PlaceBookItemFrame(item);
			items.add(frame);
		}
	}
}
