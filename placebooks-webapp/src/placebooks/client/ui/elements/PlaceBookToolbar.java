package placebooks.client.ui.elements;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookHome;
import placebooks.client.ui.PlaceBookLibrary;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.dialogs.PlaceBookAccountsDialog;
import placebooks.client.ui.dialogs.PlaceBookCreateAccountDialog;
import placebooks.client.ui.dialogs.PlaceBookLoginDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookToolbar extends Composite
{
	interface PlaceBookToolbarUiBinder extends UiBinder<Widget, PlaceBookToolbar>
	{
	}

	private static final PlaceBookToolbarUiBinder uiBinder = GWT.create(PlaceBookToolbarUiBinder.class);

	@UiField
	PlaceBookToolbarItem homeItem;

	@UiField
	PlaceBookToolbarItem createItem;

	@UiField
	PlaceBookToolbarItem libraryItem;

	@UiField
	Panel dropMenu;

	@UiField
	Label divider;

	@UiField
	Label signupLabel;

	@UiField
	HTML loginLabel;
	
	@UiField
	Panel loginPanel;

	private User user;

	private final Timer hideMenuTimer = new Timer()
	{
		@Override
		public void run()
		{
			hideMenu();
		}
	};

	private PlaceBookPlace place;

	private final RequestCallback shelfCallback = new AbstractCallback()
	{
		@Override
		public void failure(final Request request, final Response response)
		{
			setUser(null);
		}

		@Override
		public void success(final Request request, final Response response)
		{
			try
			{
				final Shelf shelf = Shelf.parse(response.getText());
				place.setShelf(shelf);
			}
			catch (final Exception e)
			{
				failure(request, response);
			}
		}
	};

	public PlaceBookToolbar()
	{
		super();
		initWidget(uiBinder.createAndBindUi(this));

		loginPanel.setVisible(false);
		
		createItem.setEnabled(false);
		libraryItem.setEnabled(false);
	}
	
	public void refresh()
	{
		libraryItem.setEnabled(!(place instanceof PlaceBookLibrary) && place.getShelf() != null);
		createItem.setEnabled(place.getShelf() != null);
		
		if (place != null && place.getShelf() != null)
		{
			setUser(place.getShelf().getUser());
		}
		else
		{
			setUser(null);
		}
	}

	@UiHandler("homeItem")
	void goHome(final ClickEvent event)
	{
		if (homeItem.isEnabled())
		{
			place.getPlaceController().goTo(new PlaceBookHome(place.getShelf()));
		}
	}

	@UiHandler("libraryItem")
	void goLibrary(final ClickEvent event)
	{
		if (libraryItem.isEnabled())
		{
			place.getPlaceController().goTo(new PlaceBookLibrary(place.getShelf()));
		}
	}

	@UiHandler("createItem")
	void goNewEditor(final ClickEvent event)
	{
		if (createItem.isEnabled())
		{
			place.getPlaceController().goTo(new PlaceBookEditor("new", place.getShelf()));
		}
	}

	public void hideMenu()
	{
		dropMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dropMenu.getElement().getStyle().setOpacity(0);
		hideMenuTimer.cancel();
	}

	@UiHandler(value={"dropMenu", "loginLabel"})
	void hideMenuTimerStart(final MouseOutEvent event)
	{
		hideMenuTimer.schedule(500);
	}

	@UiHandler("loginLabel")
	void login(final ClickEvent event)
	{
		if (user == null)
		{
			final PopupPanel dialogBox = new PopupPanel();
			dialogBox.setGlassEnabled(true);
			dialogBox.setAnimationEnabled(true);
			final PlaceBookLoginDialog account = new PlaceBookLoginDialog("Login", "Login", "Email:");
			account.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					dialogBox.hide();
					PlaceBookService.login(account.getUsername(), account.getPassword(), shelfCallback);
				}
			});
			dialogBox.add(account);
			dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
			dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
			dialogBox.setAutoHideEnabled(true);

			dialogBox.center();
			dialogBox.show();
			account.focus();
		}
	}

	@UiHandler("logout")
	void logout(final ClickEvent event)
	{
		hideMenu();
		PlaceBookService.logout(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				place.getPlaceController().goTo(new PlaceBookHome(null));
			}
		});
	}

	public void setPlace(final PlaceBookPlace place)
	{
		this.place = place;
		homeItem.setEnabled(!(place instanceof PlaceBookHome));
		libraryItem.setEnabled(!(place instanceof PlaceBookLibrary) && place.getShelf() != null);

		createItem.setEnabled(place.getShelf() != null);

		if (place != null && place.getShelf() != null)
		{
			setUser(place.getShelf().getUser());
		}
		else
		{
			PlaceBookService.getShelf(shelfCallback);
		}
	}

	private void setUser(final User user)
	{
		loginPanel.setVisible(true);
		if (this.user == user) { return; }
		this.user = user;
		if (user != null)
		{
			getElement().getStyle().setDisplay(Display.BLOCK);
			divider.setVisible(false);
			signupLabel.setVisible(false);
			loginLabel.setHTML(user.getName() + "&nbsp;<span class=\"" + Resources.INSTANCE.style().dropIcon() + "\">&#9660;</span>");

		}
		else
		{
			getElement().getStyle().setDisplay(Display.BLOCK);

			divider.setVisible(true);
			signupLabel.setVisible(true);
			loginLabel.setText("LOGIN");
		}
	}

	@UiHandler("linkedAccounts")
	void showLinkedAccountsDialog(final ClickEvent event)
	{
		hideMenu();
		final PopupPanel dialogBox = new PopupPanel();
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		final PlaceBookAccountsDialog account = new PlaceBookAccountsDialog(user);
		dialogBox.setWidget(account);

		dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
		dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
		dialogBox.setAutoHideEnabled(true);

		dialogBox.setWidth("500px");

		dialogBox.center();
		dialogBox.show();
	}

	public void showMenu(final int x, final int y)
	{
		dropMenu.getElement().getStyle().setTop(y, Unit.PX);
		dropMenu.getElement().getStyle().setLeft(x, Unit.PX);
		dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dropMenu.getElement().getStyle().setOpacity(0.9);
		hideMenuTimer.cancel();
	}

	@UiHandler("dropMenu")
	void showMenu(final MouseOverEvent event)
	{
		showMenu(dropMenu.getAbsoluteLeft(), dropMenu.getAbsoluteTop());
	}

	@UiHandler("loginLabel")
	void showMenuLogin(final MouseOverEvent event)
	{
		if (user != null)
		{
			showMenu(loginLabel.getAbsoluteLeft(), loginLabel.getAbsoluteTop() + loginLabel.getOffsetHeight());
		}
	}

	@UiHandler("signupLabel")
	void signup(final ClickEvent event)
	{
		final PopupPanel dialogBox = new PopupPanel();
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		final PlaceBookCreateAccountDialog account = new PlaceBookCreateAccountDialog();
		account.setCallback(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				dialogBox.hide();
				PlaceBookService.login(account.getEmail(), account.getPassword(), shelfCallback);
			}
		});
		dialogBox.add(account);
		dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
		dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
		dialogBox.setAutoHideEnabled(true);

		dialogBox.center();
		dialogBox.show();
	}
}