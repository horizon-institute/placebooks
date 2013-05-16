package placebooks.client.ui;

import org.wornchaos.client.controller.ControllerState;
import org.wornchaos.client.ui.View;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBooks;
import placebooks.client.controllers.PlaceBookController;
import placebooks.client.controllers.UserController;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.User;
import placebooks.client.ui.dialogs.PlaceBookConfirmDialog;
import placebooks.client.ui.dialogs.PlaceBookPermissionsDialog;
import placebooks.client.ui.dialogs.PlaceBookPublishDialog;
import placebooks.client.ui.elements.DragController;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.PlaceBookPages;
import placebooks.client.ui.elements.PlaceBookSaveItem;
import placebooks.client.ui.elements.PlaceBookToolbar;
import placebooks.client.ui.elements.PlaceBookToolbarItem;
import placebooks.client.ui.items.frames.PlaceBookItemPopupFrame;
import placebooks.client.ui.palette.Palette;
import placebooks.client.ui.places.Home;
import placebooks.client.ui.places.PlaceBook;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends PlaceBookPage implements View<PlaceBookBinder>
{
	interface PlaceBookBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final PlaceBookBookEditorUiBinder uiBinder = GWT.create(PlaceBookBookEditorUiBinder.class);

	private final PlaceBookController controller = new PlaceBookController();

	@UiField
	Panel backPanel;

	@UiField
	PlaceBookPages bookPanel;

	@UiField
	Widget loadingPanel;

	@UiField
	Palette palette;

	@UiField
	PlaceBookSaveItem saveItem;

	@UiField
	Label zoomLabel;

	@UiField
	TextBox title;

	@UiField
	PlaceBookToolbarItem actionMenu;

	@UiField
	PlaceBookToolbar toolbar;
	
	@UiField
	DropMenu dropMenu;

	private final PlaceBookItemPopupFrame.Factory factory = new PlaceBookItemPopupFrame.Factory();

	private DragController dragController;

	private int zoom = 100;

	public PlaceBookEditor(final String placebookID)
	{
		controller.load(placebookID);
	}

	public void checkAuthorized(final PlaceBookBinder binder)
	{
		if (binder == null) { return; }
		if (!UserController.getController().hasLoaded()) { return; }
		final User user = UserController.getUser();
		if (user == null)
		{
			if ("PUBLISHED".equals(binder.getState()))
			{
				PlaceBooks.goTo(new PlaceBook(binder));
			}
			else
			{
				PlaceBooks.goTo(new Home());
			}
		}

		if (binder.getOwner() == null || user.getEmail().equals(binder.getOwner().getEmail())) { return; }

		if (binder.getPermissions() != null && binder.getPermissions().keySet().size() > 0
				&& binder.getPermissions().containsKey(user.getEmail()))
		{
			if (binder.getPermissions().get(user.getEmail()).isString().stringValue().equals("R_W"))
			{
				return;
			}
			else if (binder.getPermissions().get(user.getEmail()).isString().stringValue().equals("R"))
			{
				PlaceBooks.goTo(new PlaceBook(binder));
			}
		}
	}

	@Override
	public Widget createView()
	{
		Widget editor;
		editor = uiBinder.createAndBindUi(this);

		loadingPanel.setVisible(true);

		Event.addNativePreviewHandler(new Event.NativePreviewHandler()
		{
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event)
			{
				if ((event.getTypeInt() == Event.ONMOUSEDOWN || event.getTypeInt() == Event.ONMOUSEMOVE)
						&& event.getNativeEvent().getButton() == NativeEvent.BUTTON_LEFT
						&& event.getNativeEvent().getEventTarget().toString().startsWith("<img"))
				{
					event.getNativeEvent().preventDefault();
				}
			}
		});

		dragController = new DragController(bookPanel, factory, controller, true);
		dragController.setupUIElements(backPanel);

		bookPanel.setDragController(dragController);
		palette.setDragController(dragController);

		controller.add(this);
		controller.add(bookPanel);
		controller.add(saveItem);

		final UserController userController = UserController.getController();
		userController.add(new View<User>()
		{
			@Override
			public void itemChanged(final User value)
			{
				checkAuthorized(controller.getItem());
			}
		});
		userController.load();

		// saveItem.setState(SaveState.saved);
		// saveItem.setRunnable(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// if (saveItem.getState() == SaveState.not_saved)
		// {
		// int versionNumber = 0;
		// try
		// {
		// versionNumber = placebook.getParameter("version", 0);
		// }
		// catch (final NumberFormatException e)
		// {
		// GWT.log(e.getMessage(), e);
		// }
		// placebook.setParameter("version", versionNumber + 1);
		// }
		// dataStore.put(placebook.getId(), placebook, new JSONResponse<PlaceBookBinder>()
		// {
		// @Override
		// public void handleError(final Request request, final Response response, final Throwable
		// throwable)
		// {
		// saveItem.setState(SaveState.save_error);
		// }
		//
		// @Override
		// public void handleResponse(final PlaceBookBinder binder)
		// {
		// valueChanged(binder);
		// saveItem.markSaved();
		// }
		// });
		// }
		// });

		title.setMaxLength(64);

		// if (placebook != null)
		// {
		// setPlaceBook(placebook);
		// }
		// else if (placebookID.equals("new"))
		// {
		// final PlaceBookBinder binder = PlaceBooks.getServer().parse(PlaceBookBinder.class,
		// newPlaceBook);
		// for (final placebooks.client.model.PlaceBook page : binder.getPages())
		// {
		// page.setMetadata("tempID", "" + System.currentTimeMillis());
		// }
		// setPlaceBook(binder);
		// }
		// else
		// {
		// dataStore.get(placebookID, new JSONResponse<PlaceBookBinder>()
		// {
		// @Override
		// public void handleOther(final Request request, final Response response)
		// {
		// PlaceBooks.goTo(new Home());
		// }
		//
		// @Override
		// public void handleResponse(final PlaceBookBinder binder)
		// {
		// setPlaceBook(binder);
		// }
		// });
		// }

		bookPanel.resized();
		new Timer()
		{
			@Override
			public void run()
			{
				bookPanel.resized();
				new Timer()
				{
					@Override
					public void run()
					{
						bookPanel.resized();
					}
				}.schedule(20);
			}
		}.schedule(10);

		return editor;
	}

	public DragController getController()
	{
		return dragController;
	}

	public PlaceBookSaveItem getSaveItem()
	{
		return saveItem;
	}

	@Override
	public void itemChanged(final PlaceBookBinder newPlacebook)
	{
		checkAuthorized(newPlacebook);

		if (newPlacebook.hasMetadata("title"))
		{
			Window.setTitle(newPlacebook.getMetadata("title") + " - " + uiMessages.placebooksEditor());
			title.setText(newPlacebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle(uiMessages.placebooksEditor());
			title.setText(uiMessages.noTitle());
		}

		if (PlaceBooks.getPlace() instanceof PlaceBook)
		{
			final PlaceBook place = (PlaceBook) PlaceBooks.getPlace();
			if (!place.getId().equals(newPlacebook.getId()))
			{
				History.newItem(placebooks.client.PlaceBooks.historyMapper.getToken(new PlaceBook(newPlacebook, PlaceBook.Type.edit)),
								false);
			}
		}

		loadingPanel.setVisible(false);
	}

	public void markChanged()
	{
		controller.markChanged();
	}

	@Override
	public String mayStop()
	{
		if (controller.getState() != ControllerState.saved) { return uiMessages.unsavedChanges(); }
		return super.mayStop();
	}

	@Override
	public void onStop()
	{
		palette.stop();
		// TODO saveItem.markSaved();
	}

	@UiHandler("newPage")
	void createPage(final ClickEvent event)
	{
		bookPanel.createPage();
		controller.markChanged();
	}

	@UiHandler("deletePage")
	void deletePage(final ClickEvent event)
	{
		if (bookPanel.deleteCurrentPage())
		{
			controller.markChanged();
		}
	}

	@UiHandler("deleteBook")
	void deletePlaceBook(final ClickEvent event)
	{
		final PlaceBookConfirmDialog dialog = new PlaceBookConfirmDialog(uiMessages.confirmDeleteMessage());
		dialog.setTitle(uiMessages.confirmDelete());
		dialog.setConfirmHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				PlaceBooks.getServer().deletePlaceBook(controller.getItem().getId(), new AbstractCallback()
				{
					@Override
					public void failure(final Request request, final Response response)
					{
						dialog.hide();
					}

					@Override
					public void success(final Request request, final Response response)
					{
						dialog.hide();
						PlaceBooks.goTo(new Home());
					}
				});
			}
		});

		dialog.show();
	}

	@UiHandler("permissions")
	void handleEditPermissions(final ClickEvent event)
	{
		final PlaceBookPermissionsDialog dialog = new PlaceBookPermissionsDialog(controller);
		dialog.show();
		dialog.center();
	}

	@UiHandler("title")
	void handleTitleEdit(final KeyUpEvent event)
	{
		bookPanel.getPlaceBook().setMetadata("title", title.getText());
		controller.markChanged();
	}

	@UiHandler("zoomIn")
	void handleZoomIn(final ClickEvent event)
	{
		setZoom(zoom + 20);
	}

	@UiHandler("zoomOut")
	void handleZoomOut(final ClickEvent event)
	{
		setZoom(zoom - 20);
	}

	@UiHandler("preview")
	void preview(final ClickEvent event)
	{
		PlaceBooks.goTo(new PlaceBook(controller.getItem()));
	}

	@UiHandler("publish")
	void publish(final ClickEvent event)
	{
		final PlaceBookPublishDialog publish = new PlaceBookPublishDialog(bookPanel);
		publish.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				loadingPanel.setVisible(true);
				publish.hide();
			}
		});

		publish.show();
		// publish.getElement().getStyle().setTop(50, Unit.PX);
	}

	@UiHandler("actionMenu")
	void showMenu(final ClickEvent event)
	{
		dropMenu.show(actionMenu.getAbsoluteLeft(), actionMenu.getAbsoluteTop() + actionMenu.getOffsetHeight());
	}

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		bookPanel.getElement().getStyle().setWidth(zoom, Unit.PCT);
		bookPanel.getElement().getStyle().setFontSize(zoom, Unit.PCT);
		zoomLabel.setText(zoom + "%");
		bookPanel.resized();
	}
}