package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.Resources;
import placebooks.client.model.PlaceBookBinder;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.User;
import placebooks.client.ui.dialogs.PlaceBookPublishDialog;
import placebooks.client.ui.elements.DropMenu;
import placebooks.client.ui.elements.PlaceBookInteractionHandler;
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
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
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
			if(place.book)
			{
				return "book:" + place.getKey();
			}
			return place.getKey();
		}
	}


	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	@UiTemplate("PlaceBookBookEditor.ui.xml")
	interface PlaceBookBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	private static final PlaceBookEditorUiBinder bookuiBinder = GWT.create(PlaceBookBookEditorUiBinder.class);

	
	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

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

	private PlaceBookInteractionHandler interactionHandler;

	private PlaceBookBinder placebook;

	private int zoom = 100;

	private final String placebookID;

	private final boolean book;
	
	public PlaceBookEditor(final User user, final PlaceBookBinder placebook)
	{
		super(user);
		this.placebook = placebook;
		this.placebookID = placebook.getId();
		book = false;
	}

	public PlaceBookEditor(final User user, final String placebookID)
	{
		super(user);
		if(placebookID.startsWith("book:"))
		{
			book = true;
			this.placebookID = placebookID.substring(5);			
		}
		else
		{
			book = false;
			this.placebookID = placebookID;			
		}
		this.placebook = null;
	}

	public PlaceBookInteractionHandler getDragHandler()
	{
		return interactionHandler;

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

		bookPanel.setPlaceBook(newPlacebook, factory);

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
		if(book)
		{
			editor = bookuiBinder.createAndBindUi(this);
		}
		else
		{
			editor = uiBinder.createAndBindUi(this);
		}

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

		interactionHandler = new PlaceBookInteractionHandler(bookPanel, factory, saveItem);
		interactionHandler.setupUIElements(backPanel);

		factory.setInteractionHandler(interactionHandler);

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
				palette.setPalette(items, interactionHandler);
			}
		});
	}

	@UiHandler("delete")
	void delete(final ClickEvent event)
	{
		final Panel panel = new FlowPanel();
		final PopupPanel dialogBox = new PopupPanel(true, true);
		dialogBox.getElement().getStyle().setZIndex(2000);

		final Label warning = new Label(
				"You will not be able to get your placebook back after deleting it. Are you sure?");
		final Button okbutton = new Button("Delete", new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				PlaceBookService.deletePlaceBook(placebook.getId(), new AbstractCallback()
				{
					@Override
					public void failure(final Request request, final Response response)
					{
						dialogBox.hide();
					}

					@Override
					public void success(final Request request, final Response response)
					{
						dialogBox.hide();
						getPlaceController().goTo(new PlaceBookHome());
					}
				});
			}
		});
		final Button cancelButton = new Button("Cancel", new ClickHandler()
		{
			@Override
			public void onClick(final ClickEvent event)
			{
				dialogBox.hide();
			}
		});

		panel.add(warning);
		panel.add(okbutton);
		panel.add(cancelButton);

		dialogBox.setGlassStyleName(Resources.STYLES.style().glassPanel());
		dialogBox.setStyleName(Resources.STYLES.style().popupPanel());
		dialogBox.setGlassEnabled(true);
		dialogBox.setAnimationEnabled(true);
		dialogBox.setWidget(panel);
		dialogBox.center();
		dialogBox.show();
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
		publish.center();
		publish.getElement().getStyle().setTop(50, Unit.PX);
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
		if (placebook != null && (placebook.getId() == null || !placebook.getId().equals(newPlacebook.getId())))
		{
			bookPanel.updatePlaceBook(newPlacebook);

			final PlaceBookBinder placebook = bookPanel.getPlaceBook();
			placebook.setId(newPlacebook.getId());

			saveItem.setState(SaveState.saved);

			getPlaceController().goTo(new PlaceBookEditor(getUser(), placebook));
		}
		else
		{
			placebook = newPlacebook;
			bookPanel.updatePlaceBook(newPlacebook);

			bookPanel.resized();
		}
	}
}