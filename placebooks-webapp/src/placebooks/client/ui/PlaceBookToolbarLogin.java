package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.LoginDetails;
import placebooks.client.model.Shelf;
import placebooks.client.model.User;
import placebooks.client.resources.Resources;
import placebooks.client.ui.menuItems.MenuItem;
import placebooks.client.ui.places.PlaceBookHomePlace;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class PlaceBookToolbarLogin extends FlowPanel
{
	public static interface ShelfListener
	{
		void shelfChanged(Shelf shelf);
	}

	private static boolean everytrailsUpdate = false;
	private final Label divider = new Label(" | ");
	private final FlowPanel dropMenu = new FlowPanel();
	private LoginDetails everytrailDetails = null;

	private final MouseOutHandler hideMenuHandler = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(final MouseOutEvent event)
		{
			hideMenuTimer.schedule(500);
		}
	};
	private final Timer hideMenuTimer = new Timer()
	{
		@Override
		public void run()
		{
			hideMenu();
		}
	};

	private final HTML loginLabel = new HTML("LOGIN");

	private final Collection<MenuItem> menuItems = new ArrayList<MenuItem>();

	private PlaceController placeController;

	private Shelf shelf;

	private final RequestCallback shelfCallback = new AbstractCallback()
	{
		@Override
		public void failure(final Request request, final Response response)
		{
			setShelfInternal(null);
		}

		@Override
		public void success(final Request request, final Response response)
		{
			try
			{
				setShelfInternal(Shelf.parse(response.getText()));
			}
			catch (final Exception e)
			{
				failure(request, response);
			}
		}
	};

	private ShelfListener shelfListener;

	private final Label signupLabel = new Label("SIGNUP");

	private User user;

	public PlaceBookToolbarLogin()
	{
		super();

		setStyleName(Resources.INSTANCE.style().toolbarLogin());
		add(loginLabel);
		add(divider);
		add(signupLabel);
		add(dropMenu);

		getElement().getStyle().setDisplay(Display.NONE);

		loginLabel.setStyleName(Resources.INSTANCE.style().toolbarItem());
		divider.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
		signupLabel.setStyleName(Resources.INSTANCE.style().toolbarItem());
		signupLabel.getElement().getStyle().setMarginLeft(0, Unit.PX);

		dropMenu.setStyleName(Resources.INSTANCE.style().toolbarMenu());

		dropMenu.addDomHandler(hideMenuHandler, MouseOutEvent.getType());
		dropMenu.addDomHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(final MouseOverEvent event)
			{
				showMenu(dropMenu.getAbsoluteLeft(), dropMenu.getAbsoluteTop());
			}
		}, MouseOverEvent.getType());

		add(new MenuItem("Link Everytrail Account")
		{
			@Override
			public boolean isEnabled()
			{
				return everytrailDetails == null;
			}

			@Override
			public void run()
			{
				hideMenu();
				final PopupPanel dialogBox = new PopupPanel();
				dialogBox.setGlassEnabled(true);
				dialogBox.setAnimationEnabled(true);
				final LoginDialog account = new LoginDialog("Link Everytrail Account", "Link Account",
						"Everytrail Username:");
				account.addClickHandler(new ClickHandler()
				{

					@Override
					public void onClick(final ClickEvent event)
					{
						PlaceBookService.linkAccount(	account.getUsername(), account.getPassword(), "Everytrail",
														new AbstractCallback()
														{
															@Override
															public void failure(final Request request,
																	final Response response)
															{
																account.setErrorText("Everytrail Login Failed");
															}

															@Override
															public void success(final Request request,
																	final Response response)
															{
																dialogBox.hide();
																everytrailsUpdate = true;
																PlaceBookService.everytrail(new AbstractCallback()
																{
																	@Override
																	public void success(final Request request,
																			final Response response)
																	{
																		// TODO Auto-generated
																		// method stub

																	}
																});
															}
														});
					}
				});
				dialogBox.add(account);
				dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
				dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
				dialogBox.setAutoHideEnabled(true);

				dialogBox.center();
				dialogBox.show();
			}
		});

		add(new MenuItem("Logout")
		{
			@Override
			public void run()
			{
				hideMenu();
				PlaceBookService.logout(new AbstractCallback()
				{
					@Override
					public void success(final Request request, final Response response)
					{
						placeController.goTo(new PlaceBookHomePlace(null));
					}
				});
			}
		});

		loginLabel.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				if (user == null)
				{
					final PopupPanel dialogBox = new PopupPanel();
					dialogBox.setGlassEnabled(true);
					dialogBox.setAnimationEnabled(true);
					final LoginDialog account = new LoginDialog("Login", "Login", "Email:");
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
		});

		loginLabel.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(final MouseOverEvent event)
			{
				if (user != null)
				{
					showMenu(loginLabel.getAbsoluteLeft(), loginLabel.getAbsoluteTop() + loginLabel.getOffsetHeight());
				}
			}
		});

		loginLabel.addMouseOutHandler(hideMenuHandler);

		signupLabel.addClickHandler(new ClickHandler()
		{

			@Override
			public void onClick(final ClickEvent event)
			{
				final PopupPanel dialogBox = new PopupPanel();
				dialogBox.setGlassEnabled(true);
				dialogBox.setAnimationEnabled(true);
				final CreateAccount account = new CreateAccount();
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
		});
	}

	public void add(final MenuItem item)
	{
		menuItems.add(item);
		dropMenu.add(item);
		item.setStyleName(Resources.INSTANCE.style().toolbarMenuItem());
	}

	public Shelf getShelf()
	{
		return shelf;
	}

	public void hideMenu()
	{
		dropMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		dropMenu.getElement().getStyle().setOpacity(0);
		hideMenuTimer.cancel();
	}

	public void setPlaceController(final PlaceController placeController)
	{
		this.placeController = placeController;
	}

	public void setShelf(final Shelf shelf)
	{
		if (shelf == null)
		{
			PlaceBookService.getShelf(shelfCallback);
			getElement().getStyle().setDisplay(Display.NONE);
		}
		else
		{
			setShelfInternal(shelf);
		}
	}

	private void setShelfInternal(final Shelf shelf)
	{
		getElement().getStyle().setDisplay(Display.BLOCK);
		if (this.shelf == shelf) { return; }
		this.shelf = shelf;

		if (shelf == null)
		{
			setUserInternal(null);
		}
		else
		{
			setUserInternal(shelf.getUser());
		}

		if (shelfListener != null)
		{
			shelfListener.shelfChanged(shelf);
		}
	}

	public void setShelfListener(final ShelfListener shelfListener)
	{
		this.shelfListener = shelfListener;
	}

	private void setUserInternal(final User user)
	{
		if (this.user == user) { return; }
		this.user = user;
		if (user != null)
		{
			getElement().getStyle().setDisplay(Display.BLOCK);
			divider.setVisible(false);
			signupLabel.setVisible(false);
			loginLabel.setHTML(user.getName() + "&nbsp;<span class=\"" + Resources.INSTANCE.style().dropIcon()
					+ "\">&#9660;</span>");

			for (final LoginDetails details : user.getLoginDetails())
			{
				if (details.getService().equals("Everytrail"))
				{
					everytrailDetails = details;
					if (!everytrailsUpdate)
					{
						everytrailsUpdate = true;
						PlaceBookService.everytrail(new AbstractCallback()
						{
							@Override
							public void success(final Request request, final Response response)
							{
							}
						});
					}
				}
			}
		}
		else
		{
			getElement().getStyle().setDisplay(Display.BLOCK);
			everytrailDetails = null;

			divider.setVisible(true);
			signupLabel.setVisible(true);
			loginLabel.setText("LOGIN");
		}
	}

	public void showMenu(final int x, final int y)
	{
		for (final MenuItem item : menuItems)
		{
			item.refresh();
		}
		dropMenu.getElement().getStyle().setTop(y, Unit.PX);
		dropMenu.getElement().getStyle().setLeft(x, Unit.PX);
		dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dropMenu.getElement().getStyle().setOpacity(0.9);
		hideMenuTimer.cancel();
	}
}