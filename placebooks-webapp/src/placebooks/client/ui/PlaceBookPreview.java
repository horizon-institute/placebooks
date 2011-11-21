package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.User;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.FacebookLikeButton;
import placebooks.client.ui.elements.GooglePlusOne;
import placebooks.client.ui.elements.PlaceBookCanvas;
import placebooks.client.ui.elements.PlaceBookToolbarItem;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookPreview extends PlaceBookPlace
{
	@Prefix("preview")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookPreview>
	{
		@Override
		public PlaceBookPreview getPlace(final String token)
		{
			return new PlaceBookPreview(null, token);
		}

		@Override
		public String getToken(final PlaceBookPreview place)
		{
			return place.getKey();
		}
	}

	interface PlaceBookPreviewUiBinder extends UiBinder<Widget, PlaceBookPreview>
	{
	}

	private static final PlaceBookPreviewUiBinder uiBinder = GWT.create(PlaceBookPreviewUiBinder.class);

	@UiField
	Panel canvasPanel;

	@UiField
	DropMenu dropMenu;

	@UiField
	Panel infoPanel;

	@UiField
	GooglePlusOne googlePlus;

	@UiField
	FacebookLikeButton facebookLike;

	@UiField
	Label titleLabel;

	@UiField
	Label delete;

	@UiField
	PlaceBookToolbarItem actionMenu;
	
	@UiField
	Anchor authorLabel;

	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	private PlaceBook placebook;
	private final String placebookKey;

	public PlaceBookPreview(final User user, final PlaceBook placebook)
	{
		super(user);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookPreview(final User user, final String placebookKey)
	{
		super(user);
		this.placebookKey = placebookKey;
		this.placebook = null;
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public void setPlaceBook(final PlaceBook placebook)
	{
		this.placebook = placebook;
		canvas.setPlaceBook(placebook, PlaceBookItemBlankFrame.FACTORY, false);

		titleLabel.setText(placebook.getMetadata("title"));
		authorLabel.setText(placebook.getOwner().getName());
		authorLabel.setHref("mailto:" + placebook.getOwner().getEmail());

		infoPanel.setVisible(true);

		if (placebook.getState() != null && placebook.getState().equals("PUBLISHED"))
		{
			final String url = PlaceBookService.getHostURL() + "placebooks/a/view/" + placebook.getKey();
			facebookLike.setURL(url);
			googlePlus.setURL(url);
		}

		if (placebook.hasMetadata("title"))
		{
			Window.setTitle("PlaceBooks - " + placebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle("PlaceBooks");
		}

		refresh();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget preview = uiBinder.createAndBindUi(this);

		infoPanel.setVisible(false);

		canvasPanel.add(canvas);

		toolbar.setPlace(this);

		panel.setWidget(preview);
		canvas.reflow();

		if (placebook != null)
		{
			setPlaceBook(placebook);
		}
		else
		{
			PlaceBookService.getPlaceBook(placebookKey, new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					final PlaceBook placebook = PlaceBook.parse(response.getText());
					setPlaceBook(placebook);
				}
			});
		}
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		if (getUser().getEmail().equals(placebook.getOwner().getEmail()))
		{
			PlaceBookService.deletePlaceBook(placebook.getKey(), new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					// TODO Auto-generated method stub

				}
			});
		}
	}

	String getKey()
	{
		return placebookKey;
	}

	@UiHandler("actionMenu")
	void showMenu(final ClickEvent event)
	{
		dropMenu.show(actionMenu.getAbsoluteLeft(), actionMenu.getAbsoluteTop() + actionMenu.getOffsetHeight());
	}

	private void refresh()
	{
		if (getUser() != null)
		{
			delete.setVisible(getUser().getEmail().equals(placebook.getOwner().getEmail()));
			setEnabledDropMenu(getUser().getEmail().equals(placebook.getOwner().getEmail()));
		}
		else
		{
			delete.setVisible(false);
			setEnabledDropMenu(false);
		}
	}

	private void setEnabledDropMenu(final boolean enabled)
	{
		actionMenu.setVisible(enabled);
	}
}