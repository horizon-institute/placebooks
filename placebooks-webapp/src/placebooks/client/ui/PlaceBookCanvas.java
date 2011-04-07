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
import com.google.gwt.user.client.ui.Image;
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

	//private PlaceBook placebook;

	private final Collection<PlaceBookItemFrame> items = new ArrayList<PlaceBookItemFrame>();

	public PlaceBookCanvas()
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		final PlaceBookItemFrame frame = new PlaceBookItemFrame(null, new EditablePanel("Lorem ipsum dolor sit amet, <b>consectetur</b> adipisicing elit, sed do <i>eiusmod</i> tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.<ul><li>Sed ut perspiciatis, unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam eaque ipsa, quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt, explicabo</li></ul>"));
		items.add(frame);
		canvas.add(frame);

		final PlaceBookItemFrame frame2 = new PlaceBookItemFrame(null, new Image("http://farm4.static.flickr.com/3229/2476270026_87b4f3e236.jpg"));
		items.add(frame2);
		canvas.add(frame2);

		
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		//this.placebook = placebook;

		for (int index = 0; index < placebook.getItems().length(); index++)
		{
			final PlaceBookItem item = placebook.getItems().get(index);
			final PlaceBookItemFrame frame = new PlaceBookItemFrame(item, null);
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