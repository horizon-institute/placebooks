package placebooks.client.ui;

import org.wornchaos.views.View;

import placebooks.client.PlaceBooks;
import placebooks.client.controllers.PlaceBookController;
import placebooks.client.controllers.UserController;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.User;
import placebooks.client.ui.elements.DragController;
import placebooks.client.ui.elements.FacebookLikeButton;
import placebooks.client.ui.elements.GooglePlusOne;
import placebooks.client.ui.elements.PlaceBookPages;
import placebooks.client.ui.elements.ProgressPanel;
import placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;
import placebooks.client.ui.places.Home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KioskPlaceBookView extends PlaceBookPage implements View<PlaceBookBinder>
{
	interface PlaceBookPreviewUiBinder extends UiBinder<Widget, KioskPlaceBookView>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final PlaceBookPreviewUiBinder uiBinder = GWT.create(PlaceBookPreviewUiBinder.class);

	private final PlaceBookController controller = new PlaceBookController();

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
	FlowPanel infoPanel;

	@UiField
	GooglePlusOne googlePlus;

	@UiField
	FacebookLikeButton facebookLike;

	@UiField
	Label titleLabel;

	@UiField
	ProgressPanel loadingPanel;

	@UiField
	Anchor authorLabel;

	private DragController dragController;

	public KioskPlaceBookView(final String placebookKey)
	{
		controller.load(placebookKey);
	}

	public void checkAuthorized(final PlaceBookBinder binder)
	{
		final User user = UserController.getUser();
		if(binder == null) { return; }
		if ("1".equals(binder.getState()) || "PUBLISHED".equals(binder.getState())) { return; }
		if (binder.getOwner() != null && binder.getOwner().getEmail().equals(user.getEmail())) { return; }	
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

	@UiHandler("back")
	void back(ClickEvent event)
	{
		History.back();
	}
	
	@Override
	public void onStop()
	{
		controller.remove(this);
		controller.remove(bookPanel);
		UserController.getController().remove(userView);
	}

	private void refresh()
	{

		bookPanel.resized();
	}
}