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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class PlaceBookToolbarLogin extends FlowPanel
{
	public static interface ShelfListener
	{
		void shelfChanged(Shelf shelf);
	}

	private static boolean loginAttempt = false;
	private final Label divider = new Label(" | ");
	private final FlowPanel dropMenu = new FlowPanel();

	private final MouseOutHandler hideMenuHandler = new MouseOutHandler()
	{
		@Override
		public void onMouseOut(final MouseOutEvent event)
		{
			hideMenuTimer.schedule(1000);
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

	private final Collection<MenuItem> menuItems = new ArrayList<MenuItem>();
	
	private final Label loginLabel = new Label("LOGIN");

	private Shelf shelf;

	private final RequestCallback shelfCallback = new AbstractCallback()
	{
		@Override
		public void failure(final Request request, final Response response)
		{
			setShelf(null);
		}

		@Override
		public void success(final Request request, final Response response)
		{
			try
			{
				setShelf(Shelf.parse(response.getText()));
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
				if(user == null)
				{
					return false;
				}
				for(LoginDetails details: user.getLoginDetails())
				{
					if(details.getService().equals("Everytrail"))
					{
						return false;
					}
				}
				return true;
			}

			@Override
			public void run()
			{
				final PopupPanel dialogBox = new PopupPanel();
				dialogBox.setGlassEnabled(true);
				dialogBox.setAnimationEnabled(true);
				final LoginDialog account = new LoginDialog("Link Everytrail Account", "Link Account",
						"Everytrail Username:", "et_email", "et_pass");
				account.setCallback(new AbstractCallback()
				{
					@Override
					public void success(final Request request, final Response response)
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

			}
		});

		add(new MenuItem("Logout")
		{
			@Override
			public void run()
			{
				PlaceBookService.logout(new AbstractCallback()
				{
					@Override
					public void success(final Request request, final Response response)
					{
						setShelf(null);
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
					final LoginDialog account = new LoginDialog("Login", "Login", "Email:", "pb_email", "pb_pass");
					account.setCallback(new AbstractCallback()
					{
						@Override
						public void success(final Request request, final Response response)
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

	public void setShelfListener(final ShelfListener shelfListener)
	{
		this.shelfListener = shelfListener;
	}

	public void setUser(final User user)
	{
		this.user = user;
		if (user != null)
		{
			divider.setVisible(false);
			signupLabel.setVisible(false);
			loginLabel.setText(user.getName());
		}
		else
		{
			divider.setVisible(true);
			signupLabel.setVisible(true);
			loginLabel.setText("LOGIN");

			if (!loginAttempt)
			{
				loginAttempt = true;
				PlaceBookService.getShelf(shelfCallback);
			}
		}
	}

	public void showMenu(final int x, final int y)
	{
		for(MenuItem item: menuItems)
		{
			item.refresh();
		}
		dropMenu.getElement().getStyle().setTop(y, Unit.PX);
		dropMenu.getElement().getStyle().setLeft(x, Unit.PX);
		dropMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		dropMenu.getElement().getStyle().setOpacity(0.9);
		hideMenuTimer.cancel();
	}

	void setShelf(final Shelf shelf)
	{
		this.shelf = shelf;

		setUser(shelf.getUser());

		if (shelfListener != null)
		{
			shelfListener.shelfChanged(shelf);
		}
	}
}