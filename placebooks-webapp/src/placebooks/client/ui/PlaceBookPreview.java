package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.User;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.FacebookLikeButton;
import placebooks.client.ui.elements.GooglePlusOne;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookPagesBook;
import placebooks.client.ui.elements.PlaceBookToolbarItem;
import placebooks.client.ui.elements.ProgressPanel;
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
import com.google.gwt.user.client.Timer;
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
	PlaceBookPagesBook bookPanel;

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
	ProgressPanel loadingPanel;
	
	@UiField
	Label delete;

	@UiField
	PlaceBookToolbarItem actionMenu;
	
	@UiField
	Anchor authorLabel;

	private PlaceBookController controller;
	
	private PlaceBookBinder placebook;
	private final String placebookID;

	public PlaceBookPreview(final User user, final PlaceBookBinder placebook)
	{
		super(user);
		this.placebook = placebook;
		this.placebookID = placebook.getId();
	}

	public PlaceBookPreview(final User user, final String placebookKey)
	{
		super(user);
		this.placebookID = placebookKey;
		this.placebook = null;
	}

	public PlaceBookPagesBook getCanvas()
	{
		return bookPanel;
	}

	public void setPlaceBook(final PlaceBookBinder placebook)
	{
		this.placebook = placebook;
		bookPanel.setPlaceBook(placebook, controller);

		titleLabel.setText(placebook.getMetadata("title", "No Title"));
		authorLabel.setText(placebook.getOwner().getName());
		authorLabel.setHref("mailto:" + placebook.getOwner().getEmail());

		infoPanel.setVisible(true);

		if (placebook.getState() != null && placebook.getState().equals("PUBLISHED"))
		{
			final String url = PlaceBookService.getHostURL() + "placebooks/a/view/" + placebook.getId();
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
		loadingPanel.setVisible(false);
		
		bookPanel.resized();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
	{
		final Widget preview = uiBinder.createAndBindUi(this);

		controller = new PlaceBookController(bookPanel, PlaceBookItemBlankFrame.FACTORY);
		
		infoPanel.setVisible(false);
		loadingPanel.setVisible(true);		

		toolbar.setPlace(this);

		panel.setWidget(preview);

		if (placebook != null)
		{
			setPlaceBook(placebook);
		}
		else
		{
			PlaceBookService.getPlaceBook(placebookID, new JSONResponse<PlaceBookBinder>()
			{
				@Override
				public void handleResponse(PlaceBookBinder binder)
				{
					setPlaceBook(binder);
				}
			});
		}
		
		bookPanel.resized();
		new Timer()
		{
			@Override
			public void run()
			{
				bookPanel.resized();	
			}
		}.schedule(10);
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		if (getUser().getEmail().equals(placebook.getOwner().getEmail()))
		{
			PlaceBookService.deletePlaceBook(placebook.getId(), new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					getPlaceController().goTo(new PlaceBookHome(getUser()));
				}
			});
		}
	}

	String getKey()
	{
		return placebookID;
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
		bookPanel.resized();
	}

	private void setEnabledDropMenu(final boolean enabled)
	{
		actionMenu.setVisible(enabled);
	}
}