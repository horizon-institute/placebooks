package placebooks.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.Shelf;
import placebooks.client.resources.Resources;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;
import placebooks.client.ui.items.frames.PlaceBookItemPopupFrame;
import placebooks.client.ui.menuItems.MenuItem;
import placebooks.client.ui.palette.Palette;
import placebooks.client.ui.places.PlaceBookEditorPlace;
import placebooks.client.ui.places.PlaceBookHomePlace;
import placebooks.client.ui.places.PlaceBookPreviewPlace;
import placebooks.client.ui.widget.RichTextArea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookEditor extends Composite
{
	public class SaveContext extends Timer
	{
		// private SaveState state = SaveState.saved;
		private static final int saveDelay = 2000;

		public void markChanged()
		{
			cancel();
			schedule(saveDelay);
			setState(SaveState.not_saved);
			// changed = true;
		}

		public void refreshMap()
		{
			for (final PlaceBookItemFrame itemFrame : canvas.getItems())
			{
				if (itemFrame.getItemWidget() instanceof MapItem)
				{
					((MapItem) itemFrame.getItemWidget()).refreshMarkers();
				}
			}
		}

		@Override
		public void run()
		{
			setState(SaveState.saving);
			PlaceBookService.savePlaceBook(placebook, new AbstractCallback()
			{
				@Override
				public void failure(final Request request, final Response response)
				{
					markChanged();
					setState(SaveState.save_error);
				}

				@Override
				public void success(final Request request, final Response response)
				{
					try
					{
						updatePlaceBook(PlaceBook.parse(response.getText()));
						setState(SaveState.saved);
					}
					catch (final Exception e)
					{
						failure(request, response);
					}
				}
			});
		}

		private void setState(final SaveState state)
		{
			// this.state = state;
			switch (state)
			{
				case saved:
					saveStatusPanel.setText("Saved");
					saveStatusPanel.hideImage();
					saveStatusPanel.setStyleName(Resources.INSTANCE.style().saveItemDisabled());
					break;

				case not_saved:
					saveStatusPanel.setText("Save");
					saveStatusPanel.hideImage();
					// saveStatusPanel.setResource(Resources.INSTANCE.save());
					saveStatusPanel.setStyleName(Resources.INSTANCE.style().saveItem());
					break;

				case saving:
					saveStatusPanel.setText("Saving");
					saveStatusPanel.setResource(Resources.INSTANCE.progress2());
					saveStatusPanel.setStyleName(Resources.INSTANCE.style().saveItemDisabled());
					break;

				case save_error:
					saveStatusPanel.setText("Error Saving");
					saveStatusPanel.setResource(Resources.INSTANCE.error());
					saveStatusPanel.setStyleName(Resources.INSTANCE.style().saveItem());
					break;

				default:
					break;
			}
		}
	}

	public enum SaveState
	{
		not_saved, save_error, saved, saving
	}

	interface PlaceBookEditorUiBinder extends UiBinder<Widget, PlaceBookEditor>
	{
	}

	private static final PlaceBookEditorUiBinder uiBinder = GWT.create(PlaceBookEditorUiBinder.class);

	@UiField
	PlaceBookToolbar toolbar;

	@UiField
	Panel backPanel;

	@UiField
	Panel canvasPanel;

	@UiField
	Panel loadingPanel;

	@UiField
	Palette palette;

	@UiField
	PlaceBookToolbarItem saveStatusPanel;

	@UiField
	RichTextArea title;

	@UiField
	Label zoomLabel;

	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	private final PlaceBookItemPopupFrame.Factory factory = new PlaceBookItemPopupFrame.Factory();

	private final PlaceBookInteractionHandler interactionHandler;

	private PlaceBook placebook;

	private Collection<MenuItem> menuItems = new ArrayList<MenuItem>();
	
	private final PlaceController placeController;

	private SaveContext saveContext = new SaveContext();

	private int zoom = 100;

	public PlaceBookEditor(final PlaceController placeController, final Shelf shelf)
	{
		initWidget(uiBinder.createAndBindUi(this));

		this.placeController = placeController;

		canvasPanel.add(canvas);

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

		interactionHandler = new PlaceBookInteractionHandler(canvas, factory, saveContext);
		interactionHandler.setupUIElements(backPanel);

		factory.setInteractionHandler(interactionHandler);

		saveContext.setState(SaveState.saved);

		Window.setTitle("PlaceBooks Editor");

		toolbar.setPlaceController(placeController);		
		toolbar.setShelf(shelf);
		
		menuItems.add(new MenuItem("Delete Placebook")
		{
			@Override
			public void run()
			{
				final Panel panel = new FlowPanel();
				final PopupPanel dialogBox = new PopupPanel(true, true);
				dialogBox.getElement().getStyle().setZIndex(2000);
				
				Label warning = new Label("You will not be able to get your placebook back after deleting it. Are you sure?");
				Button okbutton = new Button("Delete", new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent event)
					{
						PlaceBookService.deletePlaceBook(placebook.getKey(), new AbstractCallback()
						{
							@Override
							public void failure(Request request, Response response)
							{
								dialogBox.hide();								
							}							
							
							@Override
							public void success(Request request, Response response)
							{
								dialogBox.hide();
								placeController.goTo(new PlaceBookHomePlace());							
							}
						});						
					}
				});
				Button cancelButton = new Button("Cancel", new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent event)
					{
						dialogBox.hide();						
					}
				});
				
				panel.add(warning);
				panel.add(okbutton);
				panel.add(cancelButton);
				

				dialogBox.setGlassStyleName(Resources.INSTANCE.style().glassPanel());
				dialogBox.setStyleName(Resources.INSTANCE.style().popupPanel());
				dialogBox.setGlassEnabled(true);
				dialogBox.setAnimationEnabled(true);
				dialogBox.setWidget(panel);
				dialogBox.center();
				dialogBox.show();				
			}
		});
		menuItems.add(new MenuItem("Print Preview")
		{
			@Override
			public void run()
			{
				placeController.goTo(new PlaceBookPreviewPlace(toolbar.getShelf(), getCanvas().getPlaceBook()));
			}
		});
		menuItems.add(new MenuItem("Publish")
		{
			@Override
			public void run()
			{
				final PopupPanel dialogBox = new PopupPanel();
				dialogBox.setGlassEnabled(true);
				dialogBox.setAnimationEnabled(true);
				final PlaceBookPublish publish = new PlaceBookPublish(toolbar, canvas);
				publish.addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						dialogBox.hide();
					}
				});
				dialogBox.add(publish);

				dialogBox.setStyleName(Resources.INSTANCE.style().dialog());
				dialogBox.setGlassStyleName(Resources.INSTANCE.style().dialogGlass());
				dialogBox.setAutoHideEnabled(true);

				dialogBox.getElement().getStyle().setZIndex(1000);
				dialogBox.show();
				dialogBox.center();
				dialogBox.getElement().getStyle().setTop(50, Unit.PX);
			}
		});

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
		
		RootPanel.get().getElement().getStyle().setOverflow(Overflow.HIDDEN);		
	}

	public PlaceBookCanvas getCanvas()
	{
		return canvas;
	}

	public PlaceBookInteractionHandler getDragHandler()
	{
		return interactionHandler;

	}

	public SaveContext getSaveContext()
	{
		return saveContext;
	}

	public void markChanged()
	{
		saveContext.markChanged();
	}

	public void setPlaceBook(final PlaceBook newPlacebook)
	{
		placebook = newPlacebook;

		canvas.setPlaceBook(newPlacebook, factory, true);

		if (newPlacebook.hasMetadata("title"))
		{
			Window.setTitle(newPlacebook.getMetadata("title") + " - PlaceBooks Editor");
			title.getElement().setInnerText(newPlacebook.getMetadata("title"));
		}
		else
		{
			Window.setTitle("PlaceBooks Editor");
			title.getElement().setInnerText("No Title");
		}

		loadingPanel.setVisible(false);
		canvas.reflow();
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
					placeController.goTo(new PlaceBookHomePlace());
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

	@UiHandler("title")
	void handleTitleEdit(final KeyUpEvent event)
	{
		canvas.getPlaceBook().setMetadata("title", title.getElement().getInnerText());
		saveContext.markChanged();
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

	@UiHandler("menu")
	void handlePlaceBookMenu(final ClickEvent event)
	{
		interactionHandler.showMenu(menuItems, event.getRelativeElement().getAbsoluteLeft(), event.getRelativeElement().getAbsoluteTop() + event.getRelativeElement().getOffsetHeight(), false);
		event.stopPropagation();
	}
	
	@Override
	protected void onAttach()
	{
		super.onAttach();
		canvas.reflow();
	}

	private void setZoom(final int zoom)
	{
		this.zoom = zoom;
		canvas.getElement().getStyle().setWidth(zoom, Unit.PCT);
		canvas.getElement().getStyle().setFontSize(zoom, Unit.PCT);
		zoomLabel.setText(zoom + "%");
		for (final PlaceBookPanel panel : canvas.getPanels())
		{
			panel.reflow();
		}
	}
	
	private void updatePlaceBook(final PlaceBook newPlacebook)
	{
		if (placebook != null && (placebook.getKey() == null || !placebook.getKey().equals(newPlacebook.getKey())))
		{
			canvas.updatePlaceBook(newPlacebook);

			final PlaceBook placebook = canvas.getPlaceBook();
			placebook.setKey(newPlacebook.getKey());

			placeController.goTo(new PlaceBookEditorPlace(placebook, toolbar.getShelf()));
		}
		else
		{
			placebook = newPlacebook;
			canvas.updatePlaceBook(newPlacebook);

			if (newPlacebook.hasMetadata("title"))
			{
				Window.setTitle(newPlacebook.getMetadata("title") + " - PlaceBook Editor");
				title.getElement().setInnerText(newPlacebook.getMetadata("title"));
			}
			else
			{
				Window.setTitle("PlaceBook Editor");
				title.getElement().setInnerText("No Title");
			}

			canvas.reflow();
		}
	}
}