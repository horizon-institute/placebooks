package placebooks.client;

import placebooks.client.model.PlaceBook;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.PlaceBookItem;
import placebooks.client.model.Shelf;
import placebooks.client.resources.Resources;
import placebooks.client.ui.PlaceBookCanvas;
import placebooks.client.ui.PlaceBookList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class PlaceBookEditor implements EntryPoint
{
	private final PlaceBookCanvas canvas = new PlaceBookCanvas();

	private final PlaceBookList list = new PlaceBookList();

	private final static String newPlaceBook = "{\"items\":[], \"metadata\":{} }";
	
	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();

		list.addSelectionHandler(new SelectionChangeEvent.Handler()
		{
			@Override
			public void onSelectionChange(final SelectionChangeEvent event)
			{
				final PlaceBookEntry entry = list.getSelection();
				entry.getKey();
				list.setVisible(false);
				canvas.setVisible(true);

				if(entry.getKey().equals("new"))
				{
					canvas.setPlaceBook(PlaceBook.parse(newPlaceBook));
				}
				else
				{
					PlaceBookService.getPlaceBook(entry.getKey(), new RequestCallback()
					{
						@Override
						public void onError(final Request request, final Throwable throwable)
						{
							GWT.log("Error with " + request.toString() + " " + throwable.toString());
						}
	
						@Override
						public void onResponseReceived(final Request request, final Response response)
						{
							GWT.log("Response Code: " + response.getStatusCode());
							GWT.log(response.getText());
							if (response.getStatusCode() == 200)
							{
								final PlaceBook placebook = PlaceBook.parse(response.getText());
								canvas.setPlaceBook(placebook);
							}
						}
					});
				}
			}
		});
		canvas.setVisible(false);

		RootPanel.get().add(list);
		RootPanel.get().add(canvas);

		PlaceBookService.getShelf(new RequestCallback()
		{
			@Override
			public void onError(final Request request, final Throwable throwable)
			{
				GWT.log("Error with " + request.toString() + " " + throwable.toString());
			}

			@Override
			public void onResponseReceived(final Request request, final Response response)
			{
				if (response.getStatusCode() == 200)
				{
					GWT.log(response.getText());					
					final Shelf shelf = Shelf.parse(response.getText());
					list.setShelf(shelf);
				}
				else
				{
					GWT.log("Response Code: " + response.getStatusCode());
				}
			}
		});
		
		PlaceBookService.getPaletteItems(new RequestCallback()
		{
			
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				if (response.getStatusCode() == 200)
				{
					GWT.log(response.getText());
					final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
					canvas.setPalette(items);
				}
				else
				{
					GWT.log("Response Code: " + response.getStatusCode());
				}
				
			}
			
			@Override
			public void onError(Request request, Throwable exception)
			{
				// TODO Auto-generated method stub
				
			}
		});		

		// canvas.setPlaceBook(PlaceBook
		// .parse("{\"key\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":{\"key\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"email\":\"ktg@cs.nott.ac.uk\",\"passwordHash\":\"098f6bcd4621d373cade4e832627b4f6\",\"name\":\"Kevin Glover\",\"friends\":[]},\"timestamp\":1303214841887,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"items\":[{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8602e0001\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214841902,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test text string\"},{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test 2\"},{\"@class\":\"placebooks.model.ImageItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://farm6.static.flickr.com/5103/5634121971_cdfd1982ca.jpg\",\"metadata\":{},\"parameters\":{}}],\"metadata\":{},\"index\":null}"));
		// canvas.reflow();
	}
}