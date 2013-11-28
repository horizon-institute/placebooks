package org.placebooks.client.ui.views;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.Resources;
import org.placebooks.client.model.Entry;
import org.placebooks.client.ui.pages.PlaceBookPage;

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

	public PlaceBookEntryPreview(final Entry entry)
	{
		initWidget(uiBinder.createAndBindUi(this));

		container.getElement().getStyle()
				.setBackgroundImage("url(" + Resources.IMAGES.placebook_open().getSafeUri().asString() + ")");

		title.setText(entry.getTitle());
		description.setText(entry.getDescription());
		author.setText(entry.getOwnerName());

		if (entry.getPreviewImage() != null)
		{
			image.setUrl(PlaceBooks.getServer().getHostURL() + "command/media?hash="
					+ entry.getPreviewImage() + "&type=imageitem");
		}
		else
		{
			image.setVisible(false);
		}

		container.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				PlaceBooks.goTo(new PlaceBookPage(entry.getKey()));
			}
		}, ClickEvent.getType());

	}
}
