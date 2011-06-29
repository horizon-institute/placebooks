package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookItemDragHandler.DragStartHandler;
import placebooks.client.ui.menuItems.AddMapMenuItem;
import placebooks.client.ui.menuItems.DeletePlaceBookMenuItem;
import placebooks.client.ui.menuItems.FitToContentMenuItem;
import placebooks.client.ui.menuItems.HideTrailMenuItem;
import placebooks.client.ui.menuItems.RemoveMapMenuItem;
import placebooks.client.ui.menuItems.SetSourceURLMenuItem;
import placebooks.client.ui.menuItems.ShowTrailMenuItem;
import placebooks.client.ui.menuItems.UploadMenuItem;
import placebooks.client.ui.widget.EditablePanel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemEditableWidgetFactory extends PlaceBookItemWidgetFactory
{
	private final PlaceBookEditor editor;

	public PlaceBookItemEditableWidgetFactory(final PlaceBookEditor editor)
	{
		super();
		this.editor = editor;
	}

	@Override
	public PlaceBookItemWidget createItemWidget(final PlaceBookCanvas canvas, final PlaceBookItem item)
	{
		final PlaceBookItemEditableWidget widget = new PlaceBookItemEditableWidget(canvas, item);
		addMenuItems(widget);
		widget.setDropMenu(editor.dropMenu);
		return widget;
	}

	@Override
	protected Widget createWidget(final PlaceBookItem item)
	{
		if (item.is(ItemType.TEXT))
		{
			final EditablePanel panel = new EditablePanel(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			return panel;
		}
		return super.createWidget(item);
	}

	@Override
	protected void setupEventHandlers(final PlaceBookItemWidget itemWidget, final Widget widget)
	{
		final PlaceBookItem item = itemWidget.getItem();
		if (itemWidget instanceof PlaceBookItemEditableWidget)
		{
			final PlaceBookItemEditableWidget editable = (PlaceBookItemEditableWidget) itemWidget;
			editable.addDragStartHandler(new MouseDownHandler()
			{
				@Override
				public void onMouseDown(final MouseDownEvent event)
				{
					editor.getDragHandler().handleDragInitialization(event, item, new DragStartHandler()
					{
						@Override
						public void handleDragStart()
						{
							editor.getCanvas().remove(itemWidget);
						}
					});
				}
			});
		}
		if (item.is(ItemType.TEXT))
		{
			final EditablePanel panel = (EditablePanel) widget;
			if (itemWidget instanceof PlaceBookItemEditableWidget)
			{
				final PlaceBookItemEditableWidget editable = (PlaceBookItemEditableWidget) itemWidget;
				panel.addFocusHandler(new FocusHandler()
				{
					@Override
					public void onFocus(final FocusEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(editable);
					}
				});
				panel.addBlurHandler(new BlurHandler()
				{
					@Override
					public void onBlur(final BlurEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(null);
					}
				});
			}
			panel.addKeyUpHandler(new KeyUpHandler()
			{
				@Override
				public void onKeyUp(final KeyUpEvent event)
				{
					item.setText(panel.getElement().getInnerHTML());
					itemWidget.getPanel().reflow();
					editor.markChanged();
				}
			});
		}
		else if (item.is(ItemType.IMAGE))
		{
			final Image image = (Image) widget;
			if (itemWidget instanceof PlaceBookItemEditableWidget)
			{
				final PlaceBookItemEditableWidget editable = (PlaceBookItemEditableWidget) itemWidget;
				image.addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(final ClickEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(editable);
					}
				});
			}
		}
		else if (item.is(ItemType.AUDIO))
		{
			final Audio audio = (Audio) widget;
			if (itemWidget instanceof PlaceBookItemEditableWidget)
			{
				final PlaceBookItemEditableWidget editable = (PlaceBookItemEditableWidget) itemWidget;
				audio.addFocusHandler(new FocusHandler()
				{
					@Override
					public void onFocus(final FocusEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(editable);
					}
				});
				audio.addBlurHandler(new BlurHandler()
				{
					@Override
					public void onBlur(final BlurEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(null);
					}
				});
			}
		}
		else if (item.is(ItemType.VIDEO))
		{
			final Video video = (Video) widget;
			if (itemWidget instanceof PlaceBookItemEditableWidget)
			{
				final PlaceBookItemEditableWidget editable = (PlaceBookItemEditableWidget) itemWidget;
				video.addFocusHandler(new FocusHandler()
				{
					@Override
					public void onFocus(final FocusEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(editable);
					}
				});
				video.addBlurHandler(new BlurHandler()
				{
					@Override
					public void onBlur(final BlurEvent event)
					{
						PlaceBookItemEditableWidget.setSelected(null);
					}
				});
			}
		}
		super.setupEventHandlers(itemWidget, widget);
	}

	private void addMenuItems(final PlaceBookItemEditableWidget item)
	{
		item.add(new DeletePlaceBookMenuItem(editor.getSaveContext(), editor.getCanvas(), item));
		item.add(new AddMapMenuItem(editor.getSaveContext(), editor.getCanvas(), item));
		item.add(new RemoveMapMenuItem(editor.getSaveContext(), item));
		item.add(new ShowTrailMenuItem(editor.getSaveContext(), item));
		item.add(new HideTrailMenuItem(editor.getSaveContext(), item));
		item.add(new FitToContentMenuItem(editor.getSaveContext(), item));

		if (item.getItem().is(ItemType.TEXT))
		{
		}
		else if (item.getItem().is(ItemType.IMAGE))
		{
			item.add(new SetSourceURLMenuItem(editor.getSaveContext(), item));
			item.add(new UploadMenuItem(item));
		}
		else if (item.getItem().is(ItemType.AUDIO))
		{
			item.add(new SetSourceURLMenuItem(editor.getSaveContext(), item));
			item.add(new UploadMenuItem(item));
		}
		else if (item.getItem().is(ItemType.VIDEO))
		{
			item.add(new SetSourceURLMenuItem(editor.getSaveContext(), item));
			item.add(new UploadMenuItem(item));
		}
		else if (item.getItem().is(ItemType.GPS))
		{
			item.add(new SetSourceURLMenuItem(editor.getSaveContext(), item));
			item.add(new UploadMenuItem(item));
		}
		else if (item.getItem().is(ItemType.WEB))
		{
			item.add(new SetSourceURLMenuItem(editor.getSaveContext(), item));
		}
	}
}