package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.JSONResponse;
import placebooks.client.PlaceBookService;
import placebooks.client.model.DataStore;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.User;
import placebooks.client.ui.dialogs.PlaceBookConfirmDialog;
import placebooks.client.ui.dialogs.PlaceBookPermissionsDialog;
import placebooks.client.ui.dialogs.PlaceBookPublishDialog;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.elements.PlaceBookPages;
import placebooks.client.ui.elements.PlaceBookSaveItem;
import placebooks.client.ui.elements.PlaceBookSaveItem.SaveState;
import placebooks.client.ui.elements.PlaceBookToolbarItem;
import placebooks.client.ui.items.frames.PlaceBookItemPopupFrame;
import placebooks.client.ui.palette.Palette;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends PlaceBookPlace
{
	private static final UIMessages uiMessages = GWT.create(UIMessages.class);
	
	@Prefix("edit")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditor>
	{
		@Override
		public PlaceBookEditor getPlace(final String token)
		{
			return new PlaceBookEditor(token);
		}

		@Override
		public String getToken(final PlaceBookEditor place)
		{
			return place.getKey();
		}
	}

	interface PlaceBookBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	private static final PlaceBookBookEditorUiBinder uiBinder = GWT.create(PlaceBookBookEditorUiBinder.class);

	private final static String newPlaceBook = "{\"pages\":[{\"items\":[], \"metadata\":{} },{\"items\":[], \"metadata\":{} }]}";

	private final DataStore<PlaceBookBinder> dataStore = new DataStore<PlaceBookBinder>()
	{

		@Override
		protected String getRequestURL(final String id)
		{
			return getHostURL() + "placebooks/a/placebookbinder/" + id;
		}

		@Override
		protected String getStorageID(final String id)
		{
			if (id == null) { return null; }
			return "placebook." + id;
		}

		@Override
		protected String getStoreData(final PlaceBookBinder binder)
		{
			return "placebookbinder=" + URL.encodePathSegment(new JSONObject(binder).toString());
		}

		@Override
		protected String getStoreURL(final String id)
		{
			return getHostURL() + "placebooks/a/saveplacebookbinder";
		}
	};

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
	DropMenu dropMenu;

	private final PlaceBookItemPopupFrame.Factory factory = new PlaceBookItemPopupFrame.Factory();

	private PlaceBookController controller;

	private PlaceBookBinder placebook;

	private int zoom = 100;

	private String placebookID;

	public PlaceBookEditor(final User user, final PlaceBookBinder placebook)
	{
		super(user);
		this.placebook = placebook;
		this.placebookID = placebook.getId();
	}

	public PlaceBookEditor(final User user, final String placebookID)
	{
		super(user);
		this.placebookID = placebookID;
		this.placebook = null;
	}
	
	public PlaceBookEditor(final String placebookID)
	{
		this.placebookID = placebookID;
		this.placebook = null;
	}

	public PlaceBookController getController()
	{
		return controller;
	}

	public PlaceBookSaveItem getSaveItem()
	{
		return saveItem;
	}

	public void markChanged()
	{
		saveItem.markChanged();
	}

	@Override
	public String mayStop()
	{
		if (saveItem.getState() != SaveState.saved) { return uiMessages.unsavedChanges(); }
		return super.mayStop();
	}

	@Override
	public void onStop()
	{
		palette.stop();
		saveItem.markSaved();
	}

	public void checkAuthorized(final PlaceBookBinder binder)
	{
		if (getUser() == null)
		{
			if ("PUBLISHED".equals(binder.getState()))
			{
				getPlaceController().goTo(new PlaceBookPreview(getUser(), binder));
			}
			else
			{
				getPlaceController().goTo(new PlaceBookHome(getUser()));
			}
		}

		if(binder.getOwner() == null || getUser().getEmail().equals(binder.getOwner().getEmail()))
		{
			return;
		}

		if(binder.getPermissions() != null && binder.getPermissions().keySet().size() > 0 && binder.getPermissions().containsKey(getUser().getEmail()))
		{
			if(binder.getPermissions().get(getUser().getEmail()).isString().stringValue().equals("R_W"))
			{
				return;
			}
			else if(binder.getPermissions().get(getUser().getEmail()).isString().stringValue().equals("R"))
			{
				getPlaceController().goTo(new PlaceBookPreview(getUser(), binder));				
			}
		}
	}
	
	@Override
	public void setUser(User user)
	{
		super.setUser(user);
		if(placebook != null)
		{
			checkAuthorized(placebook);
		}
	}

	public void setPlaceBook(final PlaceBookBinder newPlacebook)
	{
		if (placebook != null)
		{
			final int currentVersion = placebook.getParameter("version", 1);
			final int newVersion = newPlacebook.getParameter("version", 1);

			GWT.log("Current: " + currentVersion + ", new: " + newVersion);

			if (currentVersion > newVersion)
			{
				controller.markChanged();
				return;
			}
			else if (currentVersion == newVersion)
			{
				updatePlaceBook(newPlacebook);
				return;
			}
		}
		
		if (isUserSet())
		{
			checkAuthorized(newPlacebook);
		}
		
		placebook = newPlacebook;

		bookPanel.setPlaceBook(newPlacebook, controller);

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

		loadingPanel.setVisible(false);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus)
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

		controller = new PlaceBookController(bookPanel, factory, saveItem);
		controller.setupUIElements(backPanel);

		palette.setControllers(controller, getPlaceController());
		
		saveItem.setState(SaveState.saved);
		saveItem.setRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				if(saveItem.getState() == SaveState.not_saved)
				{
					int versionNumber = 0;
					try
					{
						versionNumber = placebook.getParameter("version", 0);
					}
					catch (final NumberFormatException e)
					{
						GWT.log(e.getMessage(), e);
					}
					placebook.setParameter("version", versionNumber + 1);
				}
				dataStore.put(placebook.getId(), placebook, new JSONResponse<PlaceBookBinder>()
				{
					@Override
					public void handleError(final Request request, final Response response, final Throwable throwable)
					{
						saveItem.setState(SaveState.save_error);
					}

					@Override
					public void handleResponse(final PlaceBookBinder binder)
					{
						updatePlaceBook(binder);
						saveItem.markSaved();
					}
				});
			}
		});

		toolbar.setPlace(this);

		title.setMaxLength(64);

		panel.setWidget(editor);

		if (placebook != null)
		{
			setPlaceBook(placebook);
		}
		else if (placebookID.equals("new"))
		{
			final PlaceBookBinder binder = PlaceBookService.parse(PlaceBookBinder.class, newPlaceBook);
			for (final PlaceBook page : binder.getPages())
			{
				page.setMetadata("tempID", "" + System.currentTimeMillis());
			}
			setPlaceBook(binder);
		}
		else
		{
			dataStore.get(placebookID, new JSONResponse<PlaceBookBinder>()
			{
				@Override
				public void handleOther(final Request request, final Response response)
				{
					getPlaceController().goTo(new PlaceBookHome(getUser()));
				}

				@Override
				public void handleResponse(final PlaceBookBinder binder)
				{
					setPlaceBook(binder);
				}
			});
		}

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
	}

	@UiHandler("newPage")
	void createPage(final ClickEvent event)
	{
		bookPanel.createPage();
		saveItem.markChanged();
	}

	@UiHandler("deletePage")
	void deletePage(final ClickEvent event)
	{
		if (bookPanel.deleteCurrentPage())
		{
			saveItem.markChanged();
		}
	}

	@UiHandler("deleteBook")
	void deletePlaceBook(final ClickEvent event)
	{
		GWT.log("Delete Click");
		final PlaceBookConfirmDialog dialog = new PlaceBookConfirmDialog(uiMessages.confirmDeleteMessage());
		dialog.setTitle(uiMessages.confirmDelete());
		dialog.setConfirmHandler(new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				PlaceBookService.deletePlaceBook(placebook.getId(), new AbstractCallback()
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
						getPlaceController().goTo(new PlaceBookHome());
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
		saveItem.markChanged();
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
		getPlaceController().goTo(new PlaceBookPreview(getUser(), placebook));
	}

	@UiHandler("publish")
	void publish(final ClickEvent event)
	{
		final PlaceBookPublishDialog publish = new PlaceBookPublishDialog(PlaceBookEditor.this, bookPanel);
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

	private String getKey()
	{
		return placebookID;
	}

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		bookPanel.getElement().getStyle().setWidth(zoom, Unit.PCT);
		bookPanel.getElement().getStyle().setFontSize(zoom, Unit.PCT);
		zoomLabel.setText(zoom + "%");
		bookPanel.resized();
	}

	private void updatePlaceBook(final PlaceBookBinder newPlacebook)
	{
		final boolean changeURL = placebook != null
				&& (placebook.getId() == null || !placebook.getId().equals(newPlacebook.getId()));

		placebook = newPlacebook;
		bookPanel.update(newPlacebook);

		if (changeURL)
		{
			placebookID = newPlacebook.getId();

			History.newItem(placebooks.client.PlaceBookEditor.historyMapper.getToken(this), false);
		}

		bookPanel.resized();
	}
}
