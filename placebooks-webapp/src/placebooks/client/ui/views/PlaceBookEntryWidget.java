package placebooks.client.ui.views;

import placebooks.client.PlaceBooks;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.items.maps.Marker;
import placebooks.client.ui.pages.places.PlaceBook;
import placebooks.client.ui.pages.places.PlaceBook.Type;

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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEntryWidget extends Composite implements HasMouseOverHandlers, HasMouseOutHandlers
{
	interface PlaceBookEntryWidgetUiBinder extends UiBinder<Widget, PlaceBookEntryWidget>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

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
	@UiField
	Image delete;

	private Marker marker;

	private Type type;
	
	private final PlaceBookEntry entry;

	private final AsyncCallback<PlaceBookEntry> callback;

	public PlaceBookEntryWidget(final PlaceBookEntry entry, final AsyncCallback<PlaceBookEntry> callback, Type type)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.entry = entry;
		this.callback = callback;
		this.type = type;

		delete.setVisible(callback != null);
		delete.setTitle("Remove PlaceBook");

		title.setText(entry.getTitle());
		if (isPublished())
		{
			image.setResource(Resources.IMAGES.placebook_published());
			setTitle(uiMessages.viewPlaceBookPublished(entry.getTitle()));
		}
		else
		{
			image.setResource(Resources.IMAGES.placebook128());
			setTitle(uiMessages.editPlaceBook(entry.getTitle()));
		}

		if (entry.getOwnerName() != null)
		{
			author.setText(uiMessages.by(entry.getOwnerName()));
			author.setVisible(true);
		}
		else
		{
			author.setVisible(false);
		}

		if (entry.getDistance() != -1)
		{
			final double milesDist = ((entry.getDistance() * Math.PI) / 180.0) * 3966.8;
			final NumberFormat fmt = NumberFormat.getDecimalFormat();
			distance.setText(uiMessages.distance(fmt.format(milesDist)));
			distance.setVisible(true);
		}
		else
		{
			distance.setVisible(false);
		}

		markerImage.setVisible(false);
	}

	@Override
	public HandlerRegistration addMouseOutHandler(final MouseOutHandler handler)
	{
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler)
	{
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public PlaceBookEntry getEntry()
	{
		return entry;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public boolean isPublished()
	{
		return entry.getState().equals("1");
	}

	public void setMarker(final Marker marker, final ImageResource resource)
	{
		this.marker = marker;
		markerImage.setResource(resource);
		markerImage.setVisible(true);
	}

	public void setMarkerVisible(final boolean visible)
	{
		if (marker != null)
		{
			markerImage.setVisible(visible);
		}
	}

	@UiHandler("container")
	void clicked(final ClickEvent event)
	{
		if(type != null)
		{
			PlaceBooks.goTo(new PlaceBook(entry.getKey(), type));
		}
		else if(isPublished())
		{
			PlaceBooks.goTo(new PlaceBook(entry.getKey()));
		}
		else
		{
			PlaceBooks.goTo(new PlaceBook(entry.getKey(), PlaceBook.Type.edit));			
		}
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		callback.onSuccess(entry);
		event.stopPropagation();
	}
}