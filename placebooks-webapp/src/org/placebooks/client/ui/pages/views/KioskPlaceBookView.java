package org.placebooks.client.ui.pages.views;

import org.placebooks.client.PlaceBooks;
import org.placebooks.client.controllers.PlaceBookController;
import org.placebooks.client.controllers.UserController;
import org.placebooks.client.model.PlaceBook;
import org.placebooks.client.model.User;
import org.placebooks.client.ui.UIMessages;
import org.placebooks.client.ui.dialogs.PlaceBookDialog;
import org.placebooks.client.ui.items.frames.PlaceBookItemBlankFrame;
import org.placebooks.client.ui.pages.WelcomePage;
import org.placebooks.client.ui.views.DragController;
import org.placebooks.client.ui.views.PagesView;
import org.placebooks.client.ui.widgets.AndroidLink;
import org.placebooks.client.ui.widgets.FacebookLikeButton;
import org.placebooks.client.ui.widgets.GooglePlusOne;
import org.placebooks.client.ui.widgets.ProgressPanel;
import org.wornchaos.views.View;

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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class KioskPlaceBookView extends PageView implements View<PlaceBook>
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
	PagesView bookPanel;

	@UiField
	FlowPanel infoPanel;

	@UiField
	GooglePlusOne googlePlus;

	@UiField
	AndroidLink android;
	
	@UiField
	FacebookLikeButton facebookLike;

	@UiField
	Label titleLabel;

	@UiField
	ProgressPanel loadingPanel;
	
	@UiField
	Image qrcode;

	@UiField
	Anchor authorLabel;

	private DragController dragController;

	public KioskPlaceBookView(final String placebookKey)
	{
		controller.load(placebookKey);
	}

	public void checkAuthorized(final PlaceBook binder)
	{
		final User user = UserController.getUser();
		if(binder == null) { return; }
		if ("1".equals(binder.getState()) || "PUBLISHED".equals(binder.getState())) { return; }
		if (binder.getOwner() != null && binder.getOwner().getEmail().equals(user.getEmail())) { return; }	
		if (binder.getPermissions().containsKey(user.getEmail())) { return; }	
		PlaceBooks.goTo(new WelcomePage());
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

	public PagesView getCanvas()
	{
		return bookPanel;
	}

	@Override
	public void itemChanged(final PlaceBook placebook)
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

		if (placebook.getMetadata().containsKey("title"))
		{
			Window.setTitle(uiMessages.placebooks() + " - " + placebook.getMetadata().get("title"));
		}
		else
		{
			Window.setTitle(uiMessages.placebooks());
		}

		android.setPackage("org.placebooks", PlaceBooks.getServer().getHostURL() + "placebook/" + placebook.getId());
		
		refresh();
		loadingPanel.setVisible(false);

		bookPanel.resized();
	}

	@UiHandler("back")
	void back(ClickEvent event)
	{
		History.back();
	}
	
	@UiHandler("qrcode")
	void showQRCode(ClickEvent event)
	{
		PlaceBookDialog dialog = new PlaceBookDialog()
		{
			
		};
		if (controller.getItem().getMetadata().containsKey("title"))
		{
			dialog.setTitle("QR Code for " + controller.getItem().getMetadata().get("title"));
		}
		else
		{
			dialog.setTitle("QR Code");
		}

		dialog.setWidget(new Image(PlaceBooks.getServer().getHostURL() + "placebooks/a/qrcode/placebook/" + controller.getItem().getId()));
		dialog.show();
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