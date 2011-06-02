package placebooks.client;

import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookActivityMapper;
import placebooks.client.ui.places.PlaceBookHistoryMapper;
import placebooks.client.ui.places.PlaceBookListPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookEditor implements EntryPoint
{
//	private final PlaceBookCanvas canvas = new PlaceBookCanvas();
//
//	private final PlaceBookList list = new PlaceBookList();

    private Place defaultPlace = new PlaceBookListPlace();
    private SimplePanel appWidget = new SimplePanel();
	
	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();

//		list.addSelectionHandler(new SelectionChangeEvent.Handler()
//		{
//			@Override
//			public void onSelectionChange(final SelectionChangeEvent event)
//			{
//				final PlaceBookEntry entry = list.getSelection();
//				entry.getKey();
//				list.setVisible(false);
//				canvas.setVisible(true);
//
//				if (entry.getKey().equals("new"))
//				{
//					canvas.updatePlaceBook(PlaceBook.parse(newPlaceBook));
//				}
//				else
//				{
//					PlaceBookService.getPlaceBook(entry.getKey(), new AbstractCallback()
//					{
//						@Override
//						public void success(final Request request, final Response response)
//						{
//							final PlaceBook placebook = PlaceBook.parse(response.getText());
//							canvas.updatePlaceBook(placebook);
//						}
//					});
//				}
//			}
//		});
//		canvas.setVisible(false);
//
//		RootPanel.get().add(list);
//		RootPanel.get().add(canvas);
//
//		PlaceBookService.getShelf(new AbstractCallback()
//		{
//			@Override
//			public void success(final Request request, final Response response)
//			{
//				final Shelf shelf = Shelf.parse(response.getText());
//				list.setShelf(shelf);
//			}
//		});
//
//		PlaceBookService.getPaletteItems(new AbstractCallback()
//		{
//			@Override
//			public void success(final Request request, final Response response)
//			{
//				final JsArray<PlaceBookItem> items = PlaceBookItem.parseArray(response.getText());
//				canvas.setPalette(items);
//			}
//		});
		
        EventBus eventBus = new SimpleEventBus();
        PlaceController placeController = new PlaceController(eventBus);

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper = new PlaceBookActivityMapper(placeController);
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        PlaceBookHistoryMapper historyMapper= GWT.create(PlaceBookHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        RootPanel.get().add(appWidget);
        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();

		// canvas.setPlaceBook(PlaceBook
		// .parse("{\"key\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":{\"key\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"email\":\"ktg@cs.nott.ac.uk\",\"passwordHash\":\"098f6bcd4621d373cade4e832627b4f6\",\"name\":\"Kevin Glover\",\"friends\":[]},\"timestamp\":1303214841887,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"items\":[{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8602e0001\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214841902,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test text string\"},{\"@class\":\"placebooks.model.TextItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://www.google.com\",\"metadata\":{},\"parameters\":{},\"text\":\"Test 2\"},{\"@class\":\"placebooks.model.ImageItem\",\"key\":\"007393cc2f6da860012f6da8609c0002\",\"placebook\":\"007393cc2f6da860012f6da8600f0000\",\"owner\":\"007393cc2ee7d0fc012ee7d0fc4b0000\",\"timestamp\":1303214842012,\"geom\":\"POINT (52.5189367988799 -4.04983520507812)\",\"sourceURL\":\"http://farm6.static.flickr.com/5103/5634121971_cdfd1982ca.jpg\",\"metadata\":{},\"parameters\":{}}],\"metadata\":{},\"index\":null}"));
		// canvas.reflow();
	}
}