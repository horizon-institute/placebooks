package placebooks.client.ui;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.PlaceBookItem.ItemType;
import placebooks.client.resources.Resources;
import placebooks.client.ui.menuItems.AddMapMenuItem;
import placebooks.client.ui.menuItems.DeletePlaceBookMenuItem;
import placebooks.client.ui.menuItems.FitToContentMenuItem;
import placebooks.client.ui.menuItems.HideTrailMenuItem;
import placebooks.client.ui.menuItems.SetSourceURLMenuItem;
import placebooks.client.ui.menuItems.ShowTrailMenuItem;
import placebooks.client.ui.menuItems.UploadMenuItem;
import placebooks.client.ui.widget.EditablePanel;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookItemWidgetFrameFactory extends PlaceBookItemWidgetFactory
{
	private final PlaceBookEditor editor;

	public PlaceBookItemWidgetFrameFactory(final PlaceBookEditor editor)
	{
		super();
		this.editor = editor;
		setCanvas(editor.getCanvas());
	}

	@Override
	public PlaceBookItemWidget createItemWidget(final PlaceBookItem item)
	{
		final PlaceBookItemWidgetFrame itemFrame = new PlaceBookItemWidgetFrame(editor.getSaveTimer(), item);

		itemFrame.setContentWidget(createWidget(itemFrame));
		itemFrame.setDropMenu(editor.dropMenu);

		addMenuItems(itemFrame);

		itemFrame.addDragStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				editor.handleDragStart(itemFrame, event);
			}
		});

		itemFrame.addResizeStartHandler(new MouseDownHandler()
		{
			@Override
			public void onMouseDown(final MouseDownEvent event)
			{
				editor.handleResizeStart(itemFrame, event);
			}
		});

		itemFrame.addMouseOverHandler(new MouseOverHandler()
		{
			@Override
			public void onMouseOver(final MouseOverEvent event)
			{
				if (editor.dragItem == null)
				{
					itemFrame.showFrame();
				}
			}
		});

		itemFrame.addMouseOutHandler(new MouseOutHandler()
		{
			@Override
			public void onMouseOut(final MouseOutEvent event)
			{
				if (editor.dragItem == null)
				{
					itemFrame.hideFrame();
				}
			}
		});

		itemFrame.refresh();
		return itemFrame;
	}

	@Override
	protected Widget createWidget(final PlaceBookItemWidget itemWidget)
	{
		final PlaceBookItem item = itemWidget.getItem();
		if (item.is(ItemType.TEXT))
		{
			final EditablePanel panel = new EditablePanel(item.getText());
			panel.setStyleName(Resources.INSTANCE.style().textitem());
			panel.addKeyUpHandler(new KeyUpHandler()
			{
				@Override
				public void onKeyUp(final KeyUpEvent event)
				{
					item.setText(panel.getElement().getInnerHTML());
					editor.markChanged();
				}
			});

			return panel;
		}
		return super.createWidget(itemWidget);
	}

	private void addMenuItems(final PlaceBookItemWidgetFrame item)
	{
		item.addMenuItem(new DeletePlaceBookMenuItem("Delete", editor.getCanvas(), item));
		item.addMenuItem(new AddMapMenuItem("App to Map", editor.getCanvas(), item));
		item.addMenuItem(new RemoveMapMenuItem("Remove from Map", item));
		item.addMenuItem(new ShowTrailMenuItem("Show Trail", item));
		item.addMenuItem(new HideTrailMenuItem("Hide Trail", item));
		item.addMenuItem(new FitToContentMenuItem("Fit To Content", item));

		if (item.getItem().is(ItemType.TEXT))
		{
		}
		else if (item.getItem().is(ItemType.IMAGE))
		{
			item.addMenuItem(new SetSourceURLMenuItem("Set URL", item));
			item.addMenuItem(new UploadMenuItem("Upload", item));
		}
		else if (item.getItem().is(ItemType.AUDIO))
		{
			item.addMenuItem(new SetSourceURLMenuItem("Set URL", item));
			item.addMenuItem(new UploadMenuItem("Upload", item));
		}
		else if (item.getItem().is(ItemType.VIDEO))
		{
			item.addMenuItem(new SetSourceURLMenuItem("Set URL", item));
			item.addMenuItem(new UploadMenuItem("Upload", item));
		}
		else if (item.getItem().is(ItemType.GPS))
		{
			item.addMenuItem(new SetSourceURLMenuItem("Set URL", item));
			item.addMenuItem(new UploadMenuItem("Upload", item));
		}
		else if (item.getItem().is(ItemType.WEB))
		{
			item.addMenuItem(new SetSourceURLMenuItem("Set URL", item));
		}
	}
}
