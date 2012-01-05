package placebooks.client.ui.elements;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
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
				final Shelf shelf = PlaceBookService.parse(Shelf.class, response.getText());
				if(shelf != null)
				{
					place.setUser(shelf.getUser());
				}
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
		libraryItem.setEnabled(!(place instanceof PlaceBookLibrary) && place.getUser() != null);
		createItem.setEnabled(place.getUser() != null);
		
		if (place != null && place.getUser() != null)
		{
			setUser(place.getUser());
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
			place.getPlaceController().goTo(new PlaceBookHome(place.getUser()));
		}
	}

	@UiHandler("libraryItem")
	void goLibrary(final ClickEvent event)
	{
		if (libraryItem.isEnabled())
		{
			place.getPlaceController().goTo(new PlaceBookLibrary(place.getUser()));
		}
	}

	@UiHandler("createItem")
	void goNewEditor(final ClickEvent event)
	{
		if (createItem.isEnabled())
		{
			place.getPlaceController().goTo(new PlaceBookEditor(place.getUser(), "new"));
		}
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
					account.setProgress(true);
					PlaceBookService.login(account.getUsername(), account.getPassword(), new AbstractCallback()
					{
						@Override
						public void failure(Request request, Response response)
						{
							account.setProgress(false);
							if(response.getText().equals("{\"detailMessage\":\"Bad credentials\"}"))
							{
								account.setError("Login not recognised. Check username and password.");								
							}
							else if(response.getText().startsWith("{\"detailMessage\":"))
							{
								account.setError(response.getText().substring(18, response.getText().length() - 2));
							}
							else
							{
								account.setError("Error logging in");
							}
							account.center();
						}

						@Override
						public void success(Request request, Response response)
						{
							account.hide();
							try
							{
								final Shelf shelf = PlaceBookService.parse(Shelf.class, response.getText());
								if(shelf != null)
								{
									place.setUser(shelf.getUser());
								}
							}
							catch (final Exception e)
							{
								failure(request, response);
							}
							
						}
					});
				}
			});
			account.show();
			account.focus();
		}
	}

	@UiHandler("logout")
	void logout(final ClickEvent event)
	{
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
		libraryItem.setEnabled(!(place instanceof PlaceBookLibrary) && place.getUser() != null);

		createItem.setEnabled(place.getUser() != null);

		if (place != null && place.getUser() != null)
		{
			setUser(place.getUser());
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
			accountItem.setHTML(user.getName());
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
		final PlaceBookAccountsDialog account = new PlaceBookAccountsDialog(user);
		account.setTitle("Linked Accounts");
		account.show();
	}

	@UiHandler("accountItem")
	void showMenuLogin(final ClickEvent event)
	{
		if (user != null)
		{
			dropMenu.show(accountItem.getAbsoluteLeft(), accountItem.getAbsoluteTop() + accountItem.getOffsetHeight());
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
		account.show();
	}
}
