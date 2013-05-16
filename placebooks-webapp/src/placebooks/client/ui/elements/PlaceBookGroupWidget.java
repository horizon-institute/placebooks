package placebooks.client.ui.elements;

import placebooks.client.PlaceBooks;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBookGroup;
import placebooks.client.ui.places.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookGroupWidget extends Composite implements HasMouseOverHandlers, HasMouseOutHandlers
{
	interface PlaceBookEntryWidgetUiBinder extends UiBinder<Widget, PlaceBookGroupWidget>
	{
	}

	//private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static PlaceBookEntryWidgetUiBinder uiBinder = GWT.create(PlaceBookEntryWidgetUiBinder.class);

	@UiField
	Image image;
	@UiField
	Label title;
	@UiField
	Image delete;

	private final PlaceBookGroup group;

	private final AsyncCallback<PlaceBookGroup> callback;

	public PlaceBookGroupWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));

		group = null;
		callback = null;

		title.setText("Create New Group");
		image.setResource(Resources.IMAGES.placebookgroup());
		delete.setVisible(true);
		delete.setResource(Resources.IMAGES.add());
	}

	public PlaceBookGroupWidget(final PlaceBookGroup group, final AsyncCallback<PlaceBookGroup> callback)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.group = group;
		this.callback = callback;

		title.setText(group.getTitle());
		if (group.getItem().getHash() != null)
		{
			image.setUrl(group.getItem().getURL());
		}
		else
		{
			image.setResource(Resources.IMAGES.placebookgroup());
		}

		delete.setVisible(callback != null);
		delete.setTitle("Delete Group");		
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

	@UiHandler("container")
	void clicked(final ClickEvent event)
	{
		if (group == null)
		{
			PlaceBooks.goTo(new Group("new", Group.Type.edit));
		}
		else
		{
			PlaceBooks.goTo(new Group(group.getId()));
		}
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		if(callback == null)
		{
			return;
		}
		callback.onSuccess(group);
		event.stopPropagation();
	}
}