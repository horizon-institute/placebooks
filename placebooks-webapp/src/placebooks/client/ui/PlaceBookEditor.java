package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.User;
import placebooks.client.ui.dialogs.PlaceBookConfirmDialog;
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
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends PlaceBookPlace
{
	@Prefix("edit")
	public static class Tokenizer implements PlaceTokenizer<PlaceBookEditor>
	{
		@Override
		public PlaceBookEditor getPlace(final String token)
		{
			return new PlaceBookEditor(null, token);
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

	private final String placebookID;

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
		if (saveItem.getState() != SaveState.saved) { return "The current PlaceBook has unsaved changes. Are you sure you want to leave?"; }
		return super.mayStop();
	}

	public void setPlaceBook(final PlaceBookBinder newPlacebook)
	{
		placebook = newPlacebook;

		bookPanel.setPlaceBook(newPlacebook, controller);

		if (newPlacebook.hasMetadata("title"))
		{
			Window.setTitle(newPlacebook.getMetadata("title") + " - PlaceBooks Editor");
			title.setText(newPlacebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle("PlaceBooks Editor");
			title.setText("No Title");
		}

		loadingPanel.setVisible(false);
		bookPanel.resized();
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

		saveItem.setState(SaveState.saved);
		saveItem.setRunnable(new Runnable()
		{
			@Override
			public void run()
			{
				PlaceBookService.savePlaceBook(placebook, new AbstractCallback()
				{
					@Override
					public void failure(final Request request, final Response response)
					{
						saveItem.setState(SaveState.save_error);
					}

					@Override
					public void success(final Request request, final Response response)
					{
						try
						{
							updatePlaceBook(PlaceBookService.parse(PlaceBookBinder.class, response.getText()));
							saveItem.setState(SaveState.saved);
						}
						catch (final Exception e)
						{
							failure(request, response);
						}
					}
				});
			}
		});

		Window.setTitle("PlaceBooks Editor");

		toolbar.setPlace(this);

		title.setMaxLength(64);

		updatePalette();
		final Timer timer = new Timer()
		{
			@Override
			public void run()
			{
				updatePalette();
			}
		};
		timer.scheduleRepeating(120000);

		panel.setWidget(editor);

		if (placebook != null)
		{
			setPlaceBook(placebook);
		}
		else if (placebookID.equals("new"))
		{
			setPlaceBook(PlaceBookService.parse(PlaceBookBinder.class, newPlaceBook));
		}
		else
		{
			PlaceBookService.getPlaceBook(placebookID, new AbstractCallback()
			{
				@Override
				public void success(final Request request, final Response response)
				{
					setPlaceBook(PlaceBookService.parse(PlaceBookBinder.class, response.getText()));
				}
			});
		}
	}

	public void updatePalette()
	{
		PlaceBookService.getPaletteItems(new AbstractCallback()
		{
			@Override
			public void failure(final Request request, final Response response)
			{
				if (response.getStatusCode() == 401)
				{
					getPlaceController().goTo(new PlaceBookHome());
				}
			}

			@Override
			public void success(final Request request, final Response response)
			{
				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
				palette.setPalette(items, controller);
			}
		});
	}

	@UiHandler("deleteBook")
	void deletePlaceBook(final ClickEvent event)
	{
		GWT.log("Delete Click");
		final PlaceBookConfirmDialog dialog = new PlaceBookConfirmDialog("You will not be able to get your placebook back after deleting it. Are you sure?");
		dialog.setTitle("Confirm Delete");
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
		//publish.getElement().getStyle().setTop(50, Unit.PX);
	}

	@UiHandler("actionMenu")
	void showMenu(final ClickEvent event)
	{
		dropMenu.show(actionMenu.getAbsoluteLeft(), actionMenu.getAbsoluteTop() + actionMenu.getOffsetHeight());
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
		bookPanel.deleteCurrentPage();
		saveItem.markChanged();
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
		if (placebook != null && (placebook.getId() == null || !placebook.getId().equals(newPlacebook.getId())))
		{
			bookPanel.update(newPlacebook);

			final PlaceBookBinder placebook = bookPanel.getPlaceBook();
			placebook.setId(newPlacebook.getId());

			saveItem.setState(SaveState.saved);

			getPlaceController().goTo(new PlaceBookEditor(getUser(), placebook));
		}
		else
		{
			placebook = newPlacebook;
			bookPanel.update(newPlacebook);
		
			bookPanel.resized();
		}
	}
}
