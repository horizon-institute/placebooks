package placebooks.client;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class PlaceBookEditor implements EntryPoint
{
	private final PlaceBookList list = new PlaceBookList();
	
	private final PlaceBookCanvas canvas = new PlaceBookCanvas();
	
	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();
		
		list.addSelectionHandler(new SelectionChangeEvent.Handler()
		{
			@Override
			public void onSelectionChange(SelectionChangeEvent event)
			{
				PlaceBookEntry entry = list.getSelection();
				entry.getKey();
				list.setVisible(false);
				canvas.setVisible(true);
				
				PlaceBookService.getPlaceBook(entry.getKey(), new RequestCallback()
				{			
					@Override
					public void onResponseReceived(Request request, Response response)
					{
						GWT.log("Response Code: " + response.getStatusCode());
						GWT.log(response.getText());		
						if(response.getStatusCode() == 200)
						{
							PlaceBook placebook = PlaceBook.parse(response.getText());
							canvas.setPlaceBook(placebook);
						}
					}
					
					@Override
					public void onError(Request request, Throwable throwable)
					{
						GWT.log("Error with " + request.toString() + " " + throwable.toString());				
					}
				});				
			}
		});
		canvas.setVisible(false);
		
		RootPanel.get().add(list);
		RootPanel.get().add(canvas);		
				
		PlaceBookService.getShelf(new RequestCallback()
		{			
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				GWT.log("Response Code: " + response.getStatusCode());
				GWT.log(response.getText());		
				if(response.getStatusCode() == 200)
				{
					Shelf shelf = Shelf.parse(response.getText());
					list.setShelf(shelf);
				}
			}
			
			@Override
			public void onError(Request request, Throwable throwable)
			{
				GWT.log("Error with " + request.toString() + " " + throwable.toString());				
			}
		});
		
		//canvas.setPlaceBook(PlaceBook
		//		.parse("{\"key\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":{\"key\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"email\":\"ktg@cs.nott.ac.uk\",\"passwordHash\":\"098f6bcd4621d373cade4e832627b4f6\",\"name\":\"Kevin Glover\",\"friends\":[]},\"timestamp\":1303214841887,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"items\":[{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8602e0001\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214841902,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test text string\"},{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test 2\"},{\"@class\":\"placebooks.model.ImageItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://farm6.static.flickr.com/5103/5634121971_cdfd1982ca.jpg\",\"metadata\":{},\"parameters\":{}}],\"metadata\":{},\"index\":null}"));
		//canvas.reflow();
	}
}