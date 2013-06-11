package placebooks.client.ui.views;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.controllers.UserController;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.dialogs.PlaceBookAccountsDialog;
import placebooks.client.ui.dialogs.PlaceBookCreateAccountDialog;
import placebooks.client.ui.dialogs.PlaceBookLoginDialog;
import placebooks.client.ui.pages.places.Groups;
import placebooks.client.ui.pages.places.Home;
import placebooks.client.ui.pages.places.Library;
import placebooks.client.ui.pages.places.PlaceBook;
import placebooks.client.ui.widgets.DropMenu;
import placebooks.client.ui.widgets.ToolbarItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.place.shared.Place;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookToolbar extends CompositeView<User>
{
	interface PlaceBookToolbarUiBinder extends UiBinder<Widget, PlaceBookToolbar>
	{
	}

	interface ToolbarStyle extends CssResource
	{
		String toolbarMenuItem();
	}

	private static final UIMessages uiConstants = GWT.create(UIMessages.class);

	private static final PlaceBookToolbarUiBinder uiBinder = GWT.create(PlaceBookToolbarUiBinder.class);

	@UiField
	ToolbarItem homeItem;

	@UiField
	ToolbarItem createItem;

	@UiField
	ToolbarItem libraryItem;

	@UiField
	DropMenu dropMenu;

	@UiField
	ToolbarItem accountItem;

	@UiField
	ToolbarItem languageItem;

	@UiField
	Panel languagePanel;

	@UiField
	ToolbarStyle style;

	@UiField
	Panel loginPanel;

	public PlaceBookToolbar()
	{
		initWidget(uiBinder.createAndBindUi(this));

		final String[] languageNames = LocaleInfo.getAvailableLocaleNames();
		if (languageNames.length <= 3)
		{
			for (final String localeName : LocaleInfo.getAvailableLocaleNames())
			{
				if (!LocaleInfo.getCurrentLocale().getLocaleName().equals(localeName))
				{
					final String displayName = LocaleInfo.getLocaleNativeDisplayName(localeName);
					languageItem.setText(displayName);
					languageItem.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(final ClickEvent event)
						{
							final UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
							urlBuilder.setHash(Window.Location.getHash());
							urlBuilder.setHost(Window.Location.getHost());
							urlBuilder.setPath(Window.Location.getPath());
							try
							{
								urlBuilder.setPort(Integer.parseInt(Window.Location.getPort()));
							}
							catch (final Exception e)
							{

							}
							urlBuilder.setProtocol(Window.Location.getProtocol());
							for (final String key : Window.Location.getParameterMap().keySet())
							{
								urlBuilder.setParameter(key, Window.Location.getParameter(key));
							}
							urlBuilder.setParameter("locale", localeName);

							Window.Location.replace(urlBuilder.buildString());
						}
					});
				}
			}
			languagePanel.setVisible(false);
		}
		else
		{
			for (final String localeName : LocaleInfo.getAvailableLocaleNames())
			{
				if (!LocaleInfo.getCurrentLocale().getLocaleName().equals(localeName))
				{
					final String displayName = LocaleInfo.getLocaleNativeDisplayName(localeName);
					final Label label = new Label(displayName);
					label.setStyleName(style.toolbarMenuItem());
					label.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(final ClickEvent event)
						{
							final UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
							urlBuilder.setHash(Window.Location.getHash());
							urlBuilder.setHost(Window.Location.getHost());
							urlBuilder.setPath(Window.Location.getPath());
							try
							{
								urlBuilder.setPort(Integer.parseInt(Window.Location.getPort()));
							}
							catch (final Exception e)
							{

							}
							urlBuilder.setProtocol(Window.Location.getProtocol());
							for (final String key : Window.Location.getParameterMap().keySet())
							{
								urlBuilder.setParameter(key, Window.Location.getParameter(key));
							}
							urlBuilder.setParameter("locale", localeName);

							Window.Location.replace(urlBuilder.buildString());
						}
					});
					languagePanel.add(label);
				}
			}
			languageItem.setVisible(false);
		}

		UserController.getController().add(this);		
		UserController.getController().load();
		//
		// userStore.get(null, new JSONResponse<User>()
		// {
		// @Override
		// public void handleError(final Request request, final Response response, final Throwable
		// throwable)
		// {
		// valueChanged(null); // ?
		// }
		//
		// @Override
		// public void handleOther(final Request request, final Response response)
		// {
		// if (response.getStatusCode() == 401)
		// {
		// userStore.removeCached(null);
		// valueChanged(null);
		// }
		// }
		//
		// @Override
		// public void handleResponse(final User object)
		// {
		// valueChanged(object);
		// }
		// });
	}
	
	@Override
	protected void onDetach()
	{
		super.onDetach();
		UserController.getController().remove(this);
	}

	@Override
	public void itemChanged(final User user)
	{
		if (UserController.getController().hasLoaded())
		{
			if (user != null)
			{
				loginPanel.setVisible(false);
				accountItem.setVisible(true);
				accountItem.setHTML(user.getName() + " ▾");
			}
			else
			{
				loginPanel.setVisible(true);
				accountItem.setVisible(false);
			}
		}
		else
		{
			loginPanel.setVisible(false);
			accountItem.setVisible(false);
		}

		final Place place = PlaceBooks.getPlace();
		homeItem.setEnabled(!(place instanceof Home));
		libraryItem.setEnabled(!(place instanceof Library) && user != null);
		createItem.setEnabled(user != null);
	}

	@UiHandler("homeItem")
	void goHome(final ClickEvent event)
	{
		if (homeItem.isEnabled())
		{
			PlaceBooks.goTo(new Home());
		}
	}

	@UiHandler("libraryItem")
	void goLibrary(final ClickEvent event)
	{
		if (libraryItem.isEnabled())
		{
			PlaceBooks.goTo(new Library());
		}
	}

	@UiHandler("createItem")
	void goNewEditor(final ClickEvent event)
	{
		if (createItem.isEnabled())
		{
			PlaceBooks.goTo(new PlaceBook(PlaceBook.Type.create));
		}
	}

	@UiHandler("loginLabel")
	void login(final ClickEvent event)
	{
		if (UserController.getUser() == null)
		{
			final PlaceBookLoginDialog loginDialog = new PlaceBookLoginDialog(uiConstants.login(), uiConstants.login(),
					uiConstants.email() + ":");
			loginDialog.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					loginDialog.setProgress(true);
					
					UserController.getController().login(loginDialog.getUsername(), loginDialog.getPassword(),
															new AsyncCallback<Shelf>()
															{

																@Override
																public void onFailure(final Throwable caught)
																{
																	loginDialog.setProgress(false);
																	// if (response != null)
																	// {
																	// if (response
																	// .getText()
																	// .equals("{\"detailMessage\":\"Bad credentials\"}"))
																	// {
																	// loginDialog.setError(uiConstants.loginFail());
																	// }
																	// else if (response.getText()
																	// .startsWith("{\"detailMessage\":"))
																	// {
																	// loginDialog
																	// .setError(response.getText()
																	// .substring( 18,
																	// response.getText()
																	// .length() - 2));
																	// }
																	// else
																	// {
																	// loginDialog.setError(uiConstants.loginError());
																	// }
																	// }
																	// else
																	// {
																	loginDialog.setError(uiConstants.loginError());
																	// }
																	loginDialog.center();
																}

																@Override
																public void onSuccess(final Shelf shelf)
																{
																	itemChanged(shelf.getUser());
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
		UserController.getController().logout(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{

				PlaceBooks.goTo(new Home());
			}
		});
	}

	@UiHandler("groups")
	void showGroupsDialog(final ClickEvent event)
	{
		PlaceBooks.goTo(new Groups());
	}

	@UiHandler("linkedAccounts")
	void showLinkedAccountsDialog(final ClickEvent event)
	{
		final PlaceBookAccountsDialog account = new PlaceBookAccountsDialog();
		account.setTitle(uiConstants.linkedAccounts());
		account.show();
	}

	@UiHandler("accountItem")
	void showMenuLogin(final ClickEvent event)
	{
		if (UserController.getUser() != null)
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
				PlaceBooks.getServer().login(account.getEmail(), account.getPassword(), new AsyncCallback<Shelf>()
				{
					@Override
					public void onFailure(final Throwable caught)
					{
						itemChanged(null);
					}

					@Override
					public void onSuccess(final Shelf result)
					{
						itemChanged(result.getUser());
					}
				});
			}
		});
		account.show();
	}
}