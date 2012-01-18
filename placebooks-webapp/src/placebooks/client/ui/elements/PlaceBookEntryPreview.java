package placebooks.client.ui.elements;

import placebooks.client.PlaceBookService;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.PlaceBookPreview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEntryPreview extends Composite
{

	interface PlaceBookEntryPreviewUiBinder extends UiBinder<Widget, PlaceBookEntryPreview>
	{
	}

	private static PlaceBookEntryPreviewUiBinder uiBinder = GWT.create(PlaceBookEntryPreviewUiBinder.class);

	@UiField
	Label title;

	@UiField
	Image image;

	@UiField
	Label description;
	
	@UiField
	Label author;
	
	@UiField
	FlowPanel container;

	public PlaceBookEntryPreview(final PlaceBookPlace place, final PlaceBookEntry entry)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		container.getElement().getStyle().setBackgroundImage("url(" + Resources.IMAGES.placebook_open().getSafeUri().asString() + ")");
		
		title.setText(entry.getTitle());
		description.setText(entry.getDescription());
		author.setText(entry.getOwnerName());
		
		if(entry.getPreviewImage() != null)
		{
			image.setUrl(PlaceBookService.getHostURL() + "placebooks/a/admin/serve/imageitem/" + entry.getPreviewImage());
		}
		else
		{
			image.setVisible(false);
		}
		
		container.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				place.getPlaceController().goTo(new PlaceBookPreview(place.getUser(), entry.getKey()));
			}
		}, ClickEvent.getType());
		
	}
}
