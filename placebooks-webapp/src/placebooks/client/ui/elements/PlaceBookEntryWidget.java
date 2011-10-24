package placebooks.client.ui.elements;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.PlaceBookPreview;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PlaceBookEntryWidget extends FlowPanel
{
	private final Image image = new Image(Resources.INSTANCE.placebook128());
	private final Label title = new Label();
	private final Label author = new Label();

	public PlaceBookEntryWidget(final PlaceBookPlace place, final PlaceBookEntry entry)
	{
		super();
		title.setText(entry.getTitle());
		setStyleName(Resources.INSTANCE.style().placebookEntry());
		title.setStyleName(Resources.INSTANCE.style().placebookEntryText());
		if (entry.getState().equals("PUBLISHED"))
		{
			image.setResource(Resources.INSTANCE.placebook_published());
			setTitle("View " + entry.getTitle() + "(published)");
		}
		else
		{
			image.setResource(Resources.INSTANCE.placebook128());
			setTitle("Edit " + entry.getTitle());
		}

		add(image);
		add(title);

		if (entry.getOwnerName() != null)
		{
			author.setText("by " + entry.getOwnerName());
			author.setStyleName(Resources.INSTANCE.style().authorText());
			add(author);
		}

		addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				if (entry.getState().equals("PUBLISHED"))
				{
					place.getPlaceController().goTo(new PlaceBookPreview(place.getShelf(), entry.getKey()));
				}
				else
				{
					place.getPlaceController().goTo(new PlaceBookEditor(entry.getKey(), place.getShelf()));
				}
			}
		}, ClickEvent.getType());
	}
}
