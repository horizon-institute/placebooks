package org.placebooks.client.ui.views;

import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.placebooks.client.PlaceBooks;
import org.placebooks.client.Resources;
import org.placebooks.client.model.Entry;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.pages.PlaceBookPage;
import org.placebooks.client.ui.pages.PlaceBookPage.Type;
import org.wornchaos.client.server.AsyncCallback;

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

	private VectorFeature marker;

	private Type type;
	
	private final Entry entry;

	private final AsyncCallback<Entry> callback;

	public PlaceBookEntryWidget(final Entry entry, final AsyncCallback<Entry> callback, Type type)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.entry = entry;
		this.callback = callback;
		this.type = type;

		delete.setVisible(callback != null);
		delete.setTitle("Remove PlaceBook");

		if(entry.getTitle() != null)
		{
			title.setText(entry.getTitle());
		}
		else
		{
			title.setText("PlaceBook " + entry.getKey());
		}
		
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

		if (entry.getDistance() != 0)
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

	public Entry getEntry()
	{
		return entry;
	}

	public VectorFeature getMarker()
	{
		return marker;
	}

	public boolean isPublished()
	{
		return entry.getState().equals("1");
	}

	public void setMarker(final VectorFeature marker, final ImageResource resource)
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
			PlaceBooks.goTo(new PlaceBookPage(entry.getKey(), type));
		}
		else if(isPublished())
		{
			PlaceBooks.goTo(new PlaceBookPage(entry.getKey()));
		}
		else
		{
			PlaceBooks.goTo(new PlaceBookPage(entry.getKey(), PlaceBookPage.Type.edit));			
		}
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		callback.onSuccess(entry);
		event.stopPropagation();
	}
}