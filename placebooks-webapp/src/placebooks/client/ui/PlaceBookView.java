package placebooks.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.wornchaos.views.View;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.controllers.PlaceBookController;
import placebooks.client.controllers.UserController;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookGroup;
import placebooks.client.model.User;
import placebooks.client.ui.elements.DragController;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.FacebookLikeButton;
import placebooks.client.ui.elements.GooglePlusOne;
import placebooks.client.ui.elements.PlaceBookPages;
import placebooks.client.ui.elements.PlaceBookToolbar;
import placebooks.client.ui.elements.PlaceBookToolbarItem;
import placebooks.client.ui.elements.ProgressPanel;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;
import placebooks.client.ui.places.Group;
import placebooks.client.ui.places.Home;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookView extends PlaceBookPage implements View<PlaceBookBinder>
{
	interface PlaceBookPreviewUiBinder extends UiBinder<Widget, PlaceBookView>
	{
	}

	interface Style extends CssResource
	{
		String groupTag();

		String menuItem();

		String right();
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
	Label titleLabel;

	@UiField
	ProgressPanel loadingPanel;

	@UiField
	Label delete;

	@UiField
	Style style;

	@UiField
	PlaceBookToolbarItem actionMenu;

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

		for (final Widget widget : infoPanel)
		{
			if (widget instanceof Label)
			{
				infoPanel.remove(widget);
			}
		}

		for (final PlaceBookGroup group : controller.getItem().getGroups())
		{
			final Label label = new Label(group.getTitle());
			label.setStyleName(style.groupTag());
			label.addStyleName(style.right());
			label.addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(final ClickEvent event)
				{
					PlaceBooks.goTo(new Group(group.getId()));
				}
			});
			infoPanel.insert(label, 1);
		}

		bookPanel.resized();
	}
}