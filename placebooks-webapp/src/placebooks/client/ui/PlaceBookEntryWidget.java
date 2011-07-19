package placebooks.client.ui;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookEditorPlace;
import placebooks.client.ui.places.PlaceBookPreviewPlace;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PlaceBookEntryWidget extends FlowPanel
{
	private final Image image = new Image(Resources.INSTANCE.placebook());
	private final Label label = new Label();

	public PlaceBookEntryWidget(final PlaceController placeController, final PlaceBookEntry entry)
	{
		if (entry.getState().equals("PUBLISHED"))
		{
			label.setText(entry.getTitle() + " (Published)");
		}
		else
		{
			label.setText(entry.getTitle());
		}

		setStyleName(Resources.INSTANCE.style().placebookEntry());
		label.setStyleName(Resources.INSTANCE.style().placebookEntryText());
		if (entry.getState().equals("PUBLISHED"))
		{
			setTitle("View " + entry.getTitle());
		}
		else
		{
			setTitle("Edit " + entry.getTitle());
		}

		add(image);
		add(label);

		addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				if (entry.getState().equals("PUBLISHED"))
				{
					placeController.goTo(new PlaceBookPreviewPlace(entry.getKey()));
				}
				else
				{
					placeController.goTo(new PlaceBookEditorPlace(entry.getKey()));
				}
			}
		}, ClickEvent.getType());
	}
}
