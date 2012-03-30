package placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.elements.PlaceBookPage;
import placebooks.client.ui.elements.PlaceBookPages;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPublishDialog extends PlaceBookDialog
{
	interface PlaceBookPublishUiBinder extends UiBinder<Widget, PlaceBookPublishDialog>
	{
	}

	private static final PlaceBookPublishUiBinder uiBinder = GWT.create(PlaceBookPublishUiBinder.class);

	private static final String[] activities = new String[] { "Walking", "Running", "Driving", "Road biking",
																"Mountain biking", "Hiking", "Motorcycling",
																"Sightseeing", "Trail running", "Alpine skiing",
																"Kayaking / Canoeing", "Geocaching",
																"Cross-country skiing", "Flying", "Mountaineering",
																"Sailing", "Backpacking", "Train",
																"Back-country skiing", "Offroading", "Roller skating",
																"Snowshoeing", "ATV/Offroading", "Boating",
																"Relaxation", "Horseback Riding", "Photography",
																"Snowboarding", "Ice skating", "Snowmobiling",
																"Hang Gliding/Paragliding", "Fly-fishing",
																"Romantic Getaway", "Skateboarding", "Bird Watching",
																"Rock Climbing", "Paddleboarding", "Fishing", "Other:" };

	@UiField
	TextBox activity;

	@UiField
	TextArea description;

	@UiField
	Panel leftButton;

	@UiField
	ListBox activityList;

	@UiField
	TextBox location;

	@UiField
	Image placebookImage;

	@UiField
	Button publish;

	@UiField
	Panel rightButton;

	@UiField
	TextBox title;

	private final List<PlaceBookItemFrame> imageItems = new ArrayList<PlaceBookItemFrame>();

	private int index = 0;

	private final PlaceBookBinder placebook;

	private final PlaceBookPlace place;

	private boolean allowPublish = true;

	public PlaceBookPublishDialog(final PlaceBookPlace place, final PlaceBookPages canvas)
	{
		setWidget(uiBinder.createAndBindUi(this));
		setTitle("Publish PlaceBook");
		title.setMaxLength(64);
		title.setText(canvas.getPlaceBook().getMetadata("title", "No Title"));
		description.setText(canvas.getPlaceBook().getMetadata("description", ""));
		location.setText(canvas.getPlaceBook().getMetadata("location", ""));

		activity.setVisible(false);

		boolean found = false;
		for (final String item : activities)
		{
			activityList.addItem(item);
			if (!found && item.equals(canvas.getPlaceBook().getMetadata("activity", "")))
			{
				activityList.setSelectedIndex(activityList.getItemCount() - 1);
				found = true;
			}
		}

		if (!found && !"".equals(canvas.getPlaceBook().getMetadata("activity", "")))
		{
			activity.setText(canvas.getPlaceBook().getMetadata("activity", ""));
			activityList.setSelectedIndex(activityList.getItemCount() - 1);
			activity.setVisible(true);
		}

		this.placebook = canvas.getPlaceBook();
		this.place = place;

		for (final PlaceBookPage page : canvas.getPages())
		{
			for (final PlaceBookItemFrame frame : page.getItems())
			{
				if (frame.getItem().is(ItemType.IMAGE) && frame.getItem().getHash() != null)
				{
					imageItems.add(frame);
				}

				if ((frame.getItem().is(ItemType.IMAGE) || frame.getItem().is(ItemType.VIDEO) || frame.getItem()
						.is(ItemType.AUDIO)) && frame.getItem().getHash() == null)
				{
					allowPublish = false;
					setError("Cannot publish while there are items which require uploading");
				}
			}
		}

		refresh();
	}

	public void addClickHandler(final ClickHandler clickHandler)
	{
		publish.addClickHandler(clickHandler);
	}

	@UiHandler("activityList")
	void activitySelect(final ChangeEvent event)
	{
		activity.setVisible(activityList.getItemText(activityList.getSelectedIndex()).equals("Other:"));
		refresh();
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
			placebook.setMetadata("placebookImage", frame.getItem().getHash());
		}

		placebook.setMetadata("title", title.getText());
		placebook.setMetadata("location", location.getText());
		if (activityList.getItemText(activityList.getSelectedIndex()).equals("Other:"))
		{
			placebook.setMetadata("activity", activity.getText());
		}
		else
		{
			placebook.setMetadata("activity", activityList.getItemText(activityList.getSelectedIndex()));
		}
		placebook.setMetadata("description", description.getText());

		PlaceBookService.publishPlaceBook(placebook, new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final PlaceBookBinder placebook = PlaceBookService.parse(PlaceBookBinder.class, response.getText());
				place.getPlaceController().goTo(new PlaceBookPreview(place.getUser(), placebook));
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

		publish.setEnabled(allowPublish
				&& !title.getText().trim().isEmpty()
				&& (!activityList.getItemText(activityList.getSelectedIndex()).equals("Other:") || !activity.getText()
						.trim().isEmpty()) && !location.getText().trim().isEmpty());

		rightButton.setVisible(index < imageItems.size());
		leftButton.setVisible(index > 0);
	}
}
