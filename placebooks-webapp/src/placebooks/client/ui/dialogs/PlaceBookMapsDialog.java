package placebooks.client.ui.dialogs;

import java.util.List;

import placebooks.client.model.PlaceBookItem;
import placebooks.client.ui.elements.PlaceBookController;
import placebooks.client.ui.items.MapItem;
import placebooks.client.ui.items.frames.PlaceBookItemFrame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PlaceBookMapsDialog extends PlaceBookDialog
{
	interface PlaceBookMapsDialogUiBinder extends UiBinder<Widget, PlaceBookMapsDialog>
	{
	}

	private static PlaceBookMapsDialogUiBinder uiBinder = GWT.create(PlaceBookMapsDialogUiBinder.class);

	@UiField
	Panel mapPanel;

	@UiField
	ListBox mapSelect;

	@UiField
	Label mapLabel;
	
	private MapItem map;
	
	private final PlaceBookItem item;
	private final List<PlaceBookItemFrame> mapItems;
	
	private final PlaceBookController controller;

	public PlaceBookMapsDialog(final PlaceBookItem item, final List<PlaceBookItemFrame> mapItems, final PlaceBookController controller)
	{
		setWidget(uiBinder.createAndBindUi(this));
		this.controller = controller;
		this.item = item;
		this.mapItems = mapItems;
		setTitle("Locate " + item.getMetadata("title", "Item") + " on Map");
		onInitialize();
		
		String mapID = item.getMetadata("mapItemID"); 
		selectMap(mapID);
		
		if(mapID != null)
		{
			for(int index = 0; index < mapSelect.getItemCount(); index++)
			{
				if(mapID.equals(mapSelect.getValue(index)))
				{
					mapSelect.setSelectedIndex(index);
				}
			}
		}
	}

	private void onInitialize()
	{
		mapSelect.clear();
		mapSelect.addItem("Not On Any Map", "");
		
		mapPanel.clear();
		
		for(PlaceBookItemFrame item: mapItems)
		{
			mapSelect.addItem("On " + item.getItem().getMetadata("title", "Untitled") + " Map (page " + (item.getPanel().getPage().getIndex() + 1) + ")", item.getItem().getKey());
		}
	}
	
	private void selectMap(final String id)
	{
		if(id == null)
		{
			item.removeMetadata("mapItemID");
		}
		else
		{
			item.setMetadata("mapItemID", id);
		}
		
		mapPanel.clear();
		
		mapPanel.setVisible(id != null);
		mapLabel.setVisible(id != null);
		
		if(item.getGeometry() == null)
		{
			mapLabel.setText("Click to Place " + item.getMetadata("title", "Untitled") + " on Map:");
		}
		else
		{
			mapLabel.setText("Click to Move " + item.getMetadata("title", "Untitled") + " on Map:");
		}
			
		if(id != null)
		{
			for(final PlaceBookItemFrame mapItem: mapItems)
			{
				if(id.equals(mapItem.getItem().getKey()))
				{
					map = new MapItem(mapItem.getItem(), controller);
					map.refreshMarkers();
					
					mapPanel.add(map);
					
					map.moveMarker(item, new ChangeHandler()
					{
						@Override
						public void onChange(ChangeEvent event)
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
	
	@UiHandler("mapSelect")
	void mapSelected(final ChangeEvent event)
	{
		int index = mapSelect.getSelectedIndex();
		String value = mapSelect.getValue(index);
		if("".equals(value))
		{
			selectMap(null);
		}
		else
		{
			selectMap(value);
		}
		
		controller.markChanged();
//		item.getItem().setMetadata("mapItemID", mapItems.iterator().next().getKey());
//		item.getItemWidget().refresh();
//		controller.getContext().markChanged();		
	}
}
