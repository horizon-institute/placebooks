package placebooks.client.ui;

import placebooks.client.model.PlaceBookEntry;
import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookEditorPlace;
import placebooks.client.ui.places.PlaceBookPreviewPlace;

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

	public PlaceBookEntryWidget(final PlaceBookToolbar toolbar, final PlaceBookEntry entry)
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
					toolbar.getPlaceController().goTo(new PlaceBookPreviewPlace(toolbar.getShelf(), entry.getKey()));
				}
				else
				{
					toolbar.getPlaceController().goTo(new PlaceBookEditorPlace(entry.getKey(), toolbar.getShelf()));
				}
			}
		}, ClickEvent.getType());
	}
}
