package placebooks.client;

import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookActivityMapper;
import placebooks.client.ui.places.PlaceBookHistoryMapper;
import placebooks.client.ui.places.PlaceBookHomePlace;

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

    private Place defaultPlace = new PlaceBookHomePlace();
    private SimplePanel appWidget = new SimplePanel();
	
	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();
		
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
	}
}