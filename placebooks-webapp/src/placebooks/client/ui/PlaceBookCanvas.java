package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
	Panel canvas;

	private PlaceBook placebook;

	private final Collection<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	public PlaceBookCanvas()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		for (int index = 0; index < 1; index++)
		{
			final PlaceBookItemFrame frame = new PlaceBookItemFrame(null);
			items.add(frame);
			canvas.add(frame);
		}
		
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			final PlaceBookItemFrame frame = new PlaceBookItemFrame(item);
			items.add(frame);
		}
	}
	
	@UiHandler("canvas")
	void handleDrag(MouseMoveEvent event)
	{
		for(PlaceBookItemFrame frame: items)
		{
			if(frame.acceptMouseMove())
			{
				frame.handleMouseMove(event.getRelativeX(canvas.getElement()), event.getRelativeY(canvas.getElement()));
			}
		}
	}
	
	
	@UiHandler("canvas")
	void handleMouseUp(MouseUpEvent event)
	{
		for(PlaceBookItemFrame frame: items)
		{
			if(frame.acceptMouseMove())
			{
				frame.stopDrag();
			}
		}
	}	
}