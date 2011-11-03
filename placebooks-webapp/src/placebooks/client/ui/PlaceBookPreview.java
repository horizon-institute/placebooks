package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.Shelf;
import placebooks.client.ui.elements.FacebookLikeButton;
import placebooks.client.ui.elements.GooglePlusOne;
import placebooks.client.ui.elements.PlaceBookCanvas;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
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

	private boolean dropMenuEnabled = false;

	@UiField
	Panel canvasPanel;

	@UiField
	Panel dropMenu;

	@UiField
	Panel menuButton;

	@UiField
	Panel infoPanel;

	@UiField
	GooglePlusOne googlePlus;

	@UiField
	FacebookLikeButton facebookLike;

	@UiField
	Label titleLabel;

	@UiField
	Label dropArrow;

	@UiField
	Label delete;

	@UiField
	Anchor authorLabel;

	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	private PlaceBook placebook;
	private final String placebookKey;

	private final Timer hideMenuTimer = new Timer()
	{
		@Override
		public void run()
		{
			hideMenu();
		}
	};

	public PlaceBookPreview(final Shelf shelf, final PlaceBook placebook)
	{
		super(shelf);
		this.placebook = placebook;
		this.placebookKey = placebook.getKey();
	}

	public PlaceBookPreview(final Shelf shelf, final String placebookKey)
	{
		super(shelf);
		this.placebookKey = placebookKey;
		this.placebook = null;
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public void hideMenu()
	{
		dropMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dropMenu.getElement().getStyle().setOpacity(0);
		hideMenuTimer.cancel();
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

	public void showMenu(final int x, final int y)
	{
		if (dropMenuEnabled)
		{
			dropMenu.getElement().getStyle().setTop(y, Unit.PX);
			dropMenu.getElement().getStyle().setLeft(x, Unit.PX);
			dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			dropMenu.getElement().getStyle().setOpacity(0.9);
			hideMenuTimer.cancel();
		}
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget preview = uiBinder.createAndBindUi(this);

		infoPanel.setVisible(false);

		canvasPanel.add(canvas);

		toolbar.setPlace(this);

		RootPanel.get().getElement().getStyle().clearOverflow();
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
		if (getCurrentUser().getEmail().equals(placebook.getOwner().getEmail()))
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

	@UiHandler(value = { "dropMenu", "menuButton" })
	void hideMenu(final MouseOutEvent event)
	{
		hideMenuTimer.schedule(500);
	}

	@UiHandler("dropMenu")
	void showMenu(final MouseOverEvent event)
	{
		showMenu(dropMenu.getAbsoluteLeft(), dropMenu.getAbsoluteTop());
	}

	@UiHandler("menuButton")
	void showMenuButton(final MouseOverEvent event)
	{
		showMenu(menuButton.getAbsoluteLeft(), menuButton.getAbsoluteTop() + menuButton.getOffsetHeight());
	}

	@Override
	protected void shelfUpdated()
	{
		refresh();
	}

	private void refresh()
	{
		if (getCurrentUser() != null)
		{
			delete.setVisible(getCurrentUser().getEmail().equals(placebook.getOwner().getEmail()));
			setEnabledDropMenu(getCurrentUser().getEmail().equals(placebook.getOwner().getEmail()));
		}
		else
		{
			delete.setVisible(false);
			setEnabledDropMenu(false);
		}
	}

	private void setEnabledDropMenu(final boolean enabled)
	{
		dropMenuEnabled = enabled;
		dropArrow.setVisible(enabled);
		if (enabled)
		{
			menuButton.addStyleName(Resources.STYLES.style().button());
		}
		else
		{
			menuButton.removeStyleName(Resources.STYLES.style().button());
		}
	}
}