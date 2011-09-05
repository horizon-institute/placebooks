package placebooks.client;

import placebooks.client.resources.Resources;
import placebooks.client.ui.places.PlaceBookActivityMapper;
import placebooks.client.ui.places.PlaceBookHistoryMapper;
import placebooks.client.ui.places.PlaceBookHomePlace;
import placebooks.client.ui.places.PlaceBookPreviewPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PlaceBookEditor implements EntryPoint
{
	// private final PlaceBookCanvas canvas = new PlaceBookCanvas();
	//
	// private final PlaceBookList list = new PlaceBookList();

	private SimplePanel appWidget = new SimplePanel();

	@Override
	public void onModuleLoad()
	{
		Resources.INSTANCE.style().ensureInjected();

		final EventBus eventBus = (EventBus) new SimpleEventBus();
		final PlaceController placeController = new PlaceController(eventBus);

		// Start ActivityManager for the main widget with our ActivityMapper
		final ActivityMapper activityMapper = new PlaceBookActivityMapper(placeController);
		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		final PlaceBookHistoryMapper historyMapper = GWT.create(PlaceBookHistoryMapper.class);
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		Place defaultPlace = new PlaceBookHomePlace();

		if (GWT.getHostPageBaseURL().endsWith("/a/view/"))
		{
			defaultPlace = new PlaceBookPreviewPlace(null, Window.Location.getPath()
					.substring(Window.Location.getPath().lastIndexOf('/') + 1));
		}

		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get().add(appWidget);
		// Goes to the place represented on URL else default place
		historyHandler.handleCurrentHistory();
	}
}