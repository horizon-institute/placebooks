package placebooks.client.ui.elements;

import placebooks.client.Resources;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.PlaceBookPreview;
import placebooks.client.ui.openlayers.Marker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEntryWidget extends Composite implements HasMouseOverHandlers, HasMouseOutHandlers
{
	interface PlaceBookEntryWidgetUiBinder extends UiBinder<Widget, PlaceBookEntryWidget>
	{
	}

	private static PlaceBookEntryWidgetUiBinder uiBinder = GWT.create(PlaceBookEntryWidgetUiBinder.class);
	
	@UiField
	Image image;
	@UiField
	Label title;
	@UiField
	Label author;
	@UiField
	Label distance;	
	@UiField
	Image markerImage;
	
	private Marker marker;
	
	private final PlaceBookPlace place;
	private final PlaceBookEntry entry;
	
	public PlaceBookEntryWidget(final PlaceBookPlace place, final PlaceBookEntry entry)
	{
		initWidget(uiBinder.createAndBindUi(this));
		
		title.setText(entry.getTitle());
		if (entry.getState().equals("PUBLISHED"))
		{
			image.setResource(Resources.IMAGES.placebook_published());
			setTitle("View " + entry.getTitle() + "(published)");
		}
		else
		{
			image.setResource(Resources.IMAGES.placebook128());
			setTitle("Edit " + entry.getTitle());
		}

		if (entry.getOwnerName() != null)
		{
			author.setText("by " + entry.getOwnerName());
			author.setVisible(true);
		}
		else
		{
			author.setVisible(false);
		}
		
		if(entry.getDistance() != -1)
		{
			double milesDist = ((entry.getDistance() * Math.PI) / 180.0) * 3966.8;  
			NumberFormat fmt = NumberFormat.getDecimalFormat();
			distance.setText(fmt.format(milesDist) + " miles");
			distance.setVisible(true);
		}
		else
		{
			distance.setVisible(false);			
		}
		
		markerImage.setVisible(false);
		
		this.place = place;
		this.entry = entry;
	}
	
	
	
	public Marker getMarker()
	{
		return marker;
	}
	
	public void setMarker(Marker marker, ImageResource resource)
	{
		this.marker = marker;
		markerImage.setResource(resource);
		markerImage.setVisible(true);
	}
	
	public void setMarkerVisible(boolean visible)
	{
		if(marker != null)
		{
			markerImage.setVisible(visible);			
		}
	}
	
	public PlaceBookEntry getEntry()
	{
		return entry;
	}
	
	@UiHandler("container")
	void clicked(ClickEvent event)
	{
		if (entry.getState().equals("PUBLISHED"))
		{
			place.getPlaceController().goTo(new PlaceBookPreview(place.getUser(), entry.getKey()));
		}
		else
		{
			place.getPlaceController().goTo(new PlaceBookEditor(place.getUser(), entry.getKey()));
		}
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}
}