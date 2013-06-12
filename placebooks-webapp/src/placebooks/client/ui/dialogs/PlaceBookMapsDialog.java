package placebooks.client.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import placebooks.client.controllers.PlaceBookItemController;
import placebooks.client.logger.Log;
import placebooks.client.ui.UIMessages;
import placebooks.client.ui.images.markers.Markers;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class PlaceBookMapsDialog extends PlaceBookDialog
{
	public interface MarkerTemplates extends SafeHtmlTemplates
	{
		@Template("<div style=\"text-align: center; padding-top: 2px;\">{0}</div>")
		SafeHtml centeredDiv(SafeHtml image);
	}

	interface PlaceBookMapsDialogUiBinder extends UiBinder<Widget, PlaceBookMapsDialog>
	{
	}

	private static class MarkerCell extends AbstractCell<ImageResource>
	{
		private static ImageResourceRenderer renderer = new ImageResourceRenderer();

		@Override
		public void render(final com.google.gwt.cell.client.Cell.Context context, final ImageResource value,
				final SafeHtmlBuilder sb)
		{
			sb.append(MARKER_TEMPLATES.centeredDiv(renderer.render(value)));
		}
	}

	private static final UIMessages uiMessages = GWT.create(UIMessages.class);

	private static final MarkerTemplates MARKER_TEMPLATES = GWT.create(MarkerTemplates.class);

	private static PlaceBookMapsDialogUiBinder uiBinder = GWT.create(PlaceBookMapsDialogUiBinder.class);

	@UiField
	Panel mapPanel;

	@UiField
	ListBox mapSelect;

	@UiField
	Label mapLabel;

	@UiField
	CheckBox markerShow;

	@UiField
	Panel mapRootPanel;

	@UiField
	Panel markerListPanel;

	private MapItem map;

	private final PlaceBookItemController item;
	private final List<PlaceBookItemFrame> mapItems;

	private final CellList<ImageResource> markers;

	public PlaceBookMapsDialog(final PlaceBookItemController item, final List<PlaceBookItemFrame> mapItems)
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.item = item;
		this.mapItems = mapItems;
		setTitle(uiMessages.locateOnMap(item.getItem().getMetadata("title", "Item")));
		onInitialize();

		final int mapPage = item.getItem().getParameter("mapPage", -1);

		if (mapPage != -1)
		{
			final String mapPageString = Integer.toString(mapPage);
			for (int index = 0; index < mapSelect.getItemCount(); index++)
			{
				if (mapPageString.equals(mapSelect.getValue(index)))
				{
					mapSelect.setSelectedIndex(index);
				}
			}
		}

		markers = new CellList<ImageResource>(new MarkerCell());

		final List<ImageResource> markerList = new ArrayList<ImageResource>();
		for (final ResourcePrototype resource : Markers.IMAGES.getResources())
		{
			if (resource instanceof ImageResource)
			{
				markerList.add((ImageResource) resource);
			}
		}

		markers.setRowData(markerList);

		final SingleSelectionModel<ImageResource> selectionModel = new SingleSelectionModel<ImageResource>();
		selectionModel.setSelected(item.getItem().getMarkerImage(), true);		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
		{
			@Override
			public void onSelectionChange(final SelectionChangeEvent event)
			{
				final ImageResource marker = selectionModel.getSelectedObject();
				try
				{
					if (marker.getName().equals("marker"))
					{
						item.removeParameter("marker");
					}
					else
					{
						final int markerIndex = marker.getName().charAt(6);
						item.setParameter("marker", markerIndex);
					}
				}
				catch (final Exception e)
				{
					item.removeParameter("marker");
				}
				markerShow(null);
				if (map != null)
				{
					map.refreshMarkers();
				}
			}
		});
		markers.setSelectionModel(selectionModel);

		markerListPanel.add(markers);

		// markerSelect.setSelectedIndex(item.getItem().getParameter("marker", 0));
		markerShow.setValue(item.getItem().getParameter("markerShow", 0) == 1);

		selectMap(mapPage);
	}

	@UiHandler("mapSelect")
	void mapSelected(final ChangeEvent event)
	{
		final int index = mapSelect.getSelectedIndex();
		final String value = mapSelect.getValue(index);
		if ("".equals(value))
		{
			selectMap(-1);
		}
		else
		{
			selectMap(Integer.parseInt(value));
		}

		Log.info("Map Select");		
		item.markChanged();
		// item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
		// item.getItemWidget().refresh();
		// controller.getContext().markChanged();
	}

	@UiHandler("markerShow")
	void markerShow(final ValueChangeEvent<Boolean> event)
	{
		if (markerShow.getValue())
		{
			item.setParameter("markerShow", 1);
		}
		else
		{
			item.removeParameter("markerShow");
		}
	}

	private void onInitialize()
	{
		mapSelect.clear();
		mapSelect.addItem(uiMessages.itemNotOnMap(), "");

		mapPanel.clear();

		for (final PlaceBookItemFrame item : mapItems)
		{
			final String title = item.getItem().getMetadata("title");
			if (title == null || title.equals("Map"))
			{
				mapSelect.addItem(	uiMessages.onMap(item.getColumn().getPage().getIndex() + 1),
									Integer.toString(item.getColumn().getPage().getIndex()));
			}
			else
			{
				mapSelect.addItem(	uiMessages.onMap(title, item.getColumn().getPage().getIndex() + 1),
									Integer.toString(item.getColumn().getPage().getIndex()));
			}
		}
	}

	private void selectMap(final int page)
	{
		if (page == -1)
		{
			item.removeParameter("mapPage");
		}
		else
		{
			item.setParameter("mapPage", page);
		}

		mapPanel.clear();

		mapRootPanel.setVisible(page != -1);
		markerShow.setVisible(page != -1);

		if (item.getItem().getGeometry() == null)
		{
			mapLabel.setText(uiMessages.clickMapPlace(item.getItem().getMetadata("title", "Untitled")));
		}
		else
		{
			mapLabel.setText(uiMessages.clickMapMove(item.getItem().getMetadata("title", "Untitled")));
		}

		if (page != -1)
		{
			for (final PlaceBookItemFrame mapItem : mapItems)
			{
				if (page == mapItem.getColumn().getPage().getIndex())
				{
					map = new MapItem(mapItem.getItemWidget().getController());
					map.refreshMarkers();

					mapPanel.add(map);

					map.moveMarker(item, new ChangeHandler()
					{
						@Override
						public void onChange(final ChangeEvent event)
						{
							mapItem.getItemWidget().refresh();
						}
					});
					break;
				}
			}
		}

		center();
	}
}
