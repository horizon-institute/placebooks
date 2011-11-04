package placebooks.client.ui.elements;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.Resources;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.ui.PlaceBookEditor;
import placebooks.client.ui.PlaceBookHome;
import placebooks.client.ui.PlaceBookLibrary;
import placebooks.client.ui.PlaceBookPlace;
import placebooks.client.ui.dialogs.PlaceBookAccountsDialog;
import placebooks.client.ui.dialogs.PlaceBookCreateAccountDialog;
import placebooks.client.ui.dialogs.PlaceBookLoginDialog;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
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
	DropMenu dropMenu;

	@UiField
	PlaceBookToolbarItem accountItem;

	@UiField
	Panel loginPanel;

	private User user;

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

	@UiHandler(value={"dropMenu", "accountItem"})
	void hideMenuTimerStart(final MouseOutEvent event)
	{
		dropMenu.startHideMenu();
	}

	@UiHandler("loginLabel")
	void login(final ClickEvent event)
	{
		if (user == null)
		{
			final PlaceBookLoginDialog account = new PlaceBookLoginDialog("Login", "Login", "Email:");
			account.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					account.hide();
					PlaceBookService.login(account.getUsername(), account.getPassword(), shelfCallback);
				}
			});
			account.center();
			account.show();
			account.focus();
		}
	}

	@UiHandler("logout")
	void logout(final ClickEvent event)
	{
		dropMenu.hideMenu();
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
			loginPanel.setVisible(false);
			accountItem.setVisible(true);
			accountItem.setHTML(user.getName() + "&nbsp;<span class=\"" + Resources.STYLES.style().dropIcon() + "\">&#9660;</span>");

		}
		else
		{
			loginPanel.setVisible(true);
			accountItem.setVisible(false);
		}
	}

	@UiHandler("linkedAccounts")
	void showLinkedAccountsDialog(final ClickEvent event)
	{
		dropMenu.hideMenu();
		final PlaceBookAccountsDialog account = new PlaceBookAccountsDialog(user);
		account.setWidth("500px");
		account.center();
		account.show();
	}

	@UiHandler("dropMenu")
	void showMenu(final MouseOverEvent event)
	{
		dropMenu.showMenu(dropMenu.getAbsoluteLeft(), dropMenu.getAbsoluteTop());
	}

	@UiHandler("accountItem")
	void showMenuLogin(final MouseOverEvent event)
	{
		if (user != null)
		{
			dropMenu.showMenu(accountItem.getAbsoluteLeft(), accountItem.getAbsoluteTop() + accountItem.getOffsetHeight());
		}
	}

	@UiHandler("signupLabel")
	void signup(final ClickEvent event)
	{
		final PlaceBookCreateAccountDialog account = new PlaceBookCreateAccountDialog();
		account.setCallback(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				account.hide();
				PlaceBookService.login(account.getEmail(), account.getPassword(), shelfCallback);
			}
		});
		account.center();
		account.show();
	}
}