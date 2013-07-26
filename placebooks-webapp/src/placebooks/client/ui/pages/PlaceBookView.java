package placebooks.client.ui.pages;

import java.util.HashMap;
import java.util.Map;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.controllers.PlaceBookController;
import placebooks.client.controllers.UserController;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookGroup;
import placebooks.client.model.User;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.dialogs.PlaceBookDialog;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;
import placebooks.client.ui.pages.places.Group;
import placebooks.client.ui.pages.places.Home;
import placebooks.client.ui.views.DragController;
import placebooks.client.ui.views.PlaceBookPages;
import placebooks.client.ui.views.PlaceBookToolbar;
import placebooks.client.ui.views.View;
import placebooks.client.ui.widgets.DropMenu;
import placebooks.client.ui.widgets.FacebookLikeButton;
import placebooks.client.ui.widgets.GooglePlusOne;
import placebooks.client.ui.widgets.ProgressPanel;
import placebooks.client.ui.widgets.ToolbarItem;
import placebooks.client.ui.widgets.ToolbarLink;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookView extends Page implements View<PlaceBookBinder>
{
	interface PlaceBookPreviewUiBinder extends UiBinder<Widget, PlaceBookView>
	{
	}

	interface Style extends CssResource
	{
		String groupTag();

		String menuItem();
	
		String inline();
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final PlaceBookPreviewUiBinder uiBinder = GWT.create(PlaceBookPreviewUiBinder.class);

	private final PlaceBookController controller = new PlaceBookController();

	private final Map<String, Label> groupMenuItems = new HashMap<String, Label>();

	private View<User> userView = new View<User>()
	{
		@Override
		public void itemChanged(final User value)
		{
			if (controller.getItem() != null)
			{
				checkAuthorized(controller.getItem());
				refresh();
			}
		}
	};

	@UiField
	PlaceBookPages bookPanel;

	@UiField
	DropMenu dropMenu;

	@UiField
	FlowPanel infoPanel;

	@UiField
	PlaceBookToolbar toolbar;
	
	@UiField
	GooglePlusOne googlePlus;

	@UiField
	FacebookLikeButton facebookLike;

	@UiField
	Image qrcode;
	
	@UiField
	Label titleLabel;

	@UiField
	FlowPanel groups;
	
	@UiField
	ProgressPanel loadingPanel;

	@UiField
	Label delete;

	@UiField
	Style style;

	@UiField
	ToolbarItem actionMenu;

	@UiField
	Anchor authorLabel;

	private DragController dragController;

	public PlaceBookView(final String placebookKey)
	{
		controller.load(placebookKey);
	}

	public void checkAuthorized(final PlaceBookBinder binder)
	{
		final User user = UserController.getUser();
		if ("PUBLISHED".equals(binder.getState())) { return; }
		if (binder.getOwner() == null) { return; }
		if (user.getEmail().equals(binder.getOwner().getEmail()))
		{
			UserController.getController().setItem(binder.getOwner());
			return;
		}
		if (binder.getPermissions().containsKey(user.getEmail())) { return; }
		PlaceBooks.goTo(new Home());
	}

	@Override
	public Widget createView()
	{
		final Widget preview = uiBinder.createAndBindUi(this);

		dragController = new DragController(bookPanel, PlaceBookItemBlankFrame.FACTORY, controller, false);

		bookPanel.setDragController(dragController);

		controller.add(this);
		controller.add(bookPanel);
		UserController.getController().add(userView);

		infoPanel.setVisible(false);
		loadingPanel.setVisible(true);

		bookPanel.resized();
		new Timer()
		{
			@Override
			public void run()
			{
				bookPanel.resized();
			}
		}.schedule(10);

		return preview;
	}

	@UiHandler("qrcode")
	void showQRCode(ClickEvent event)
	{
		PlaceBookDialog dialog = new PlaceBookDialog()
		{
			
		};
		if (controller.getItem().hasMetadata("title"))
		{
			dialog.setTitle("QR Code for " + controller.getItem().getMetadata("title"));
		}
		else
		{
			dialog.setTitle("QR Code");
		}

		dialog.setWidget(new Image(PlaceBooks.getServer().getHostURL() + "placebooks/a/qrcode/placebook/" + controller.getItem().getId()));
		dialog.show();
	}
	
	public PlaceBookPages getCanvas()
	{
		return bookPanel;
	}

	@Override
	public void itemChanged(final PlaceBookBinder placebook)
	{
		checkAuthorized(placebook);

		titleLabel.setText(placebook.getMetadata("title", "No Title"));
		authorLabel.setText(placebook.getOwner().getName());
		authorLabel.setHref("mailto:" + placebook.getOwner().getEmail());

		infoPanel.setVisible(true);
		
		qrcode.setUrl(PlaceBooks.getServer().getHostURL() + "placebooks/a/qrcode/placebook/" + controller.getItem().getId());			

		if ("PUBLISHED".equals(placebook.getState()))
		{
			final String url = PlaceBooks.getServer().getHostURL() + "placebooks/a/view/" + placebook.getId();
			facebookLike.setURL(url);
			googlePlus.setURL(url);
		}

		if (placebook.hasMetadata("title"))
		{
			Window.setTitle(uiMessages.placebooks() + " - " + placebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle(uiMessages.placebooks());
		}

		refresh();
		loadingPanel.setVisible(false);

		bookPanel.resized();
	}

	@Override
	public void onStop()
	{
		controller.remove(this);
		controller.remove(bookPanel);
		UserController.getController().remove(userView);
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		if (UserController.getUser().getEmail().equals(controller.getItem().getOwner().getEmail()))
		{
			PlaceBooks.getServer().deletePlaceBook(controller.getItem().getId(), new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					PlaceBooks.goTo(new Home());
				}
			});
		}
	}

	@UiHandler("actionMenu")
	void showMenu(final ClickEvent event)
	{
		dropMenu.show(actionMenu.getAbsoluteLeft(), actionMenu.getAbsoluteTop() + actionMenu.getOffsetHeight());
	}

	private boolean hasGroup(final PlaceBookBinder binder, final PlaceBookGroup group)
	{
		for (final PlaceBookGroup testGroup : binder.getGroups())
		{
			if (group.getId().equals(testGroup.getId())) { return true; }
		}
		return false;
	}

	private void refresh()
	{
		final User user = UserController.getUser();
		if (user != null)
		{
			delete.setVisible(user.getEmail().equals(controller.getItem().getOwner().getEmail()));
			actionMenu.setVisible(user.getEmail().equals(controller.getItem().getOwner().getEmail()));

			if(controller.getItem() != null && controller.getItem().getState().equals("PUBLISHED"))
			{
				for (final PlaceBookGroup group : user.getGroups())
				{
					if (hasGroup(controller.getItem(), group))
					{
						if (groupMenuItems.containsKey(group.getId()))
						{
							final Label label = groupMenuItems.get(group.getId());
							dropMenu.remove(label);
							groupMenuItems.remove(group.getId());
						}
						continue;
					}
					if (groupMenuItems.containsKey(group.getId()))
					{
						continue;
					}
					final Label label = new Label("Add to Group " + group.getTitle());
					label.setStyleName(style.menuItem());
					label.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(final ClickEvent event)
						{
							PlaceBooks.getServer().addGroup(controller.getItem().getId(), group.getId(), controller);
						}
					});
					groupMenuItems.put(group.getId(), label);
					dropMenu.add(label);
				}
			}
		}
		else
		{
			delete.setVisible(false);
			actionMenu.setVisible(false);
		}

		groups.clear();
		
		for (final PlaceBookGroup group : controller.getItem().getGroups())
		{
			final ToolbarLink link = new ToolbarLink();
			link.setText(group.getTitle());
			link.setStyleName(style.groupTag());
			link.addStyleName(style.inline());
			link.setURL(GWT.getHostPageBaseURL() + "#" + PlaceBooks.getToken(new Group(group.getId())));
			groups.add(link);
		}

		bookPanel.resized();
	}
}