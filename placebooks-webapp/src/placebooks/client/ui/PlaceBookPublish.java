package placebooks.client.ui;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.places.PlaceBookPreviewPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPublish extends Composite
{
	interface PlaceBookPublishUiBinder extends UiBinder<Widget, PlaceBookPublish>
	{
	}

	private static final PlaceBookPublishUiBinder uiBinder = GWT.create(PlaceBookPublishUiBinder.class);

	@UiField
	TextBox title;

	@UiField
	TextBox location;
	
	@UiField
	TextBox activity;
	
	@UiField
	TextArea description;
	
	@UiField
	Image placebookImage;
		
	@UiField
	Button publish;
	
	@UiField
	Image leftButton;

	@UiField
	Image rightButton;
	
	private final PlaceBook placebook;
	
	private int index = 0;
	
	private final PlaceController placeController;
	
	private final List<PlaceBookItemFrame> imageItems = new ArrayList<PlaceBookItemFrame>();
	
	public PlaceBookPublish(PlaceController placeController, PlaceBookCanvas canvas)
	{
		initWidget(uiBinder.createAndBindUi(this));
		title.setText(canvas.getPlaceBook().getMetadata("title", "No Title"));
		description.setText(canvas.getPlaceBook().getMetadata("description", ""));
		activity.setText(canvas.getPlaceBook().getMetadata("activity", ""));
		location.setText(canvas.getPlaceBook().getMetadata("location", ""));		
		
		this.placebook = canvas.getPlaceBook();
		this.placeController = placeController;
		
		for(PlaceBookItemFrame frame: canvas.getItems())
		{
			if(frame.getItem().is(ItemType.IMAGE))
			{
				imageItems.add(frame);
			}
		}
		
		refresh();
	}
	
	public void addClickHandler(final ClickHandler clickHandler)
	{
		publish.addClickHandler(clickHandler);
	}
	
	@UiHandler(value={"location", "activity", "title"})
	void handleChangeValue(KeyUpEvent event)
	{
		refresh();
	}
	
	@UiHandler("leftButton")
	void handleLeftClick(ClickEvent event)
	{
		if(index > 0)
		{
			index--;
			refresh();
		}
	}
	
	@UiHandler("publish")
	void handlePublish(ClickEvent event)
	{
		if(index >= 0 && index < imageItems.size())
		{
			PlaceBookItemFrame frame = imageItems.get(index);
			placebook.setMetadata("placebookImage", frame.getItem().getKey());			
		}
		
		placebook.setMetadata("title", title.getText());
		placebook.setMetadata("location", location.getText());
		placebook.setMetadata("activity", activity.getText());
		placebook.setMetadata("description", description.getText());
		
		PlaceBookService.publishPlaceBook(placebook, new AbstractCallback()
		{
			@Override
			public void success(Request request, Response response)
			{
				PlaceBook placebook = PlaceBook.parse(response.getText());
				placeController.goTo(new PlaceBookPreviewPlace(placebook));
			}
		});
	}	
	
	@UiHandler("rightButton")
	void handleRightClick(ClickEvent event)
	{
		if(index < imageItems.size())
		{
			index++;
			refresh();
		}
	}
	
	private void refresh()
	{
		if(index >= 0 && index < imageItems.size())
		{
			PlaceBookItemFrame frame = imageItems.get(index);
			placebookImage.setUrl(frame.getItem().getURL());
		}
		
		publish.setEnabled(!title.getText().trim().isEmpty() && !activity.getText().trim().isEmpty() && !location.getText().trim().isEmpty());
		
		rightButton.setVisible(index < imageItems.size());
		leftButton.setVisible(index > 0);		
	}
}