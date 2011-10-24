package placebooks.client.ui.elements;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
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
	TextBox activity;

	@UiField
	TextArea description;

	@UiField
	Image leftButton;

	@UiField
	TextBox location;

	@UiField
	Image placebookImage;

	@UiField
	Button publish;

	@UiField
	Image rightButton;

	@UiField
	TextBox title;

	private final List<PlaceBookItemFrame> imageItems = new ArrayList<PlaceBookItemFrame>();

	private int index = 0;

	private final PlaceBook placebook;

	private final PlaceBookPlace place;

	public PlaceBookPublish(final PlaceBookPlace place, final PlaceBookCanvas canvas)
	{
		initWidget(uiBinder.createAndBindUi(this));
		title.setMaxLength(64);
		title.setText(canvas.getPlaceBook().getMetadata("title", "No Title"));
		description.setText(canvas.getPlaceBook().getMetadata("description", ""));
		activity.setText(canvas.getPlaceBook().getMetadata("activity", ""));
		location.setText(canvas.getPlaceBook().getMetadata("location", ""));

		this.placebook = canvas.getPlaceBook();
		this.place = place;

		for (final PlaceBookItemFrame frame : canvas.getItems())
		{
			if (frame.getItem().is(ItemType.IMAGE))
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

	@UiHandler(value = { "location", "activity", "title" })
	void handleChangeValue(final KeyUpEvent event)
	{
		refresh();
	}

	@UiHandler("leftButton")
	void handleLeftClick(final ClickEvent event)
	{
		if (index > 0)
		{
			index--;
			refresh();
		}
	}

	@UiHandler("publish")
	void handlePublish(final ClickEvent event)
	{
		if (index >= 0 && index < imageItems.size())
		{
			final PlaceBookItemFrame frame = imageItems.get(index);
			placebook.setMetadata("placebookImage", frame.getItem().getKey());
		}

		placebook.setMetadata("title", title.getText());
		placebook.setMetadata("location", location.getText());
		placebook.setMetadata("activity", activity.getText());
		placebook.setMetadata("description", description.getText());

		PlaceBookService.publishPlaceBook(placebook, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final PlaceBook placebook = PlaceBook.parse(response.getText());
				place.getPlaceController().goTo(new PlaceBookPreview(place.getShelf(), placebook));
			}
		});
	}

	@UiHandler("rightButton")
	void handleRightClick(final ClickEvent event)
	{
		if (index < imageItems.size())
		{
			index++;
			refresh();
		}
	}

	private void refresh()
	{
		if (index >= 0 && index < imageItems.size())
		{
			final PlaceBookItemFrame frame = imageItems.get(index);
			placebookImage.setUrl(frame.getItem().getURL());
		}

		publish.setEnabled(!title.getText().trim().isEmpty() && !activity.getText().trim().isEmpty()
				&& !location.getText().trim().isEmpty());

		rightButton.setVisible(index < imageItems.size());
		leftButton.setVisible(index > 0);
	}
}