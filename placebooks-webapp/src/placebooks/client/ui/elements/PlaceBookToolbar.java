package placebooks.client.ui.elements;

import placebooks.client.AbstractCallback;
import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.DataStore;
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

	private final DataStore<User> userStore = new DataStore<User>()
	{
		@Override
		protected String getRequestURL(final String id)
		{
			return getHostURL() + "placebooks/a/currentUser";
		}

		@Override
		protected String getStorageID(final String id)
		{
			return "current.user";
		}
	};

	public PlaceBookToolbar()
	{
		super();
		initWidget(uiBinder.createAndBindUi(this));

		loginPanel.setVisible(false);

		accountItem.setVisible(false);

		createItem.setEnabled(false);
		libraryItem.setEnabled(false);
	}

	public void setPlace(final PlaceBookPlace place)
	{
		this.place = place;

		if (place != null && place.getUser() != null)
		{
			setUser(place.getUser());
		}
		else
		{
			userStore.get(null, new JSONResponse<User>()
			{
				@Override
				public void handleError(final Request request, final Response response, final Throwable throwable)
				{
					place.setUser(null);
				}

				@Override
				public void handleOther(final Request request, final Response response)
				{
					place.setUser(null);
					if(response.getStatusCode() == 401)
					{
						userStore.removeCached(null);
					}
				}

				@Override
				public void handleResponse(final User object)
				{
					place.setUser(object);
				}
			});
		}
	}

	public void setUser(final User user)
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
		refreshItems();
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
			final PlaceBookLoginDialog loginDialog = new PlaceBookLoginDialog("Login", "Login", "Email:");
			loginDialog.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					loginDialog.setProgress(true);
					PlaceBookService.login(loginDialog.getUsername(), loginDialog.getPassword(), new JSONResponse<Shelf>()
					{
						@Override
						public void handleError(final Request request, final Response response,
								final Throwable throwable)
						{
							loginDialog.setProgress(false);
							if (response != null)
							{
								if (response.getText().equals("{\"detailMessage\":\"Bad credentials\"}"))
								{
									loginDialog.setError("Login not recognised. Check username and password.");
								}
								else if (response.getText().startsWith("{\"detailMessage\":"))
								{
									loginDialog.setError(response.getText().substring(18, response.getText().length() - 2));
								}
								else
								{
									loginDialog.setError("Error logging in");
								}
							}
							else
							{
								loginDialog.setError("Error logging in");
							}
							loginDialog.center();
						}						

						@Override
						public void handleResponse(final Shelf shelf)
						{
							place.setUser(shelf.getUser());
							loginDialog.hide();
						}
					});
				}
			});
			loginDialog.show();
			loginDialog.focus();
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
				userStore.removeCached(null);
				place.getPlaceController().goTo(new PlaceBookHome(null));
			}
		});
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
				PlaceBookService.login(account.getEmail(), account.getPassword(), new JSONResponse<Shelf>()
				{

					@Override
					public void handleError(final Request request, final Response response, final Throwable throwable)
					{
						place.setUser(null);
					}

					@Override
					public void handleOther(final Request request, final Response response)
					{
						place.setUser(null);
					}

					@Override
					public void handleResponse(final Shelf object)
					{
						place.setUser(object.getUser());
					}
				});
			}
		});
		account.show();
	}

	private void refreshItems()
	{
		homeItem.setEnabled(!(place instanceof PlaceBookHome));
		libraryItem.setEnabled(!(place instanceof PlaceBookLibrary) && place.getUser() != null);
		createItem.setEnabled(place.getUser() != null);
	}
}