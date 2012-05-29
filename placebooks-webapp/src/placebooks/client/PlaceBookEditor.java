package placebooks.client;

import placebooks.client.ui.PlaceBookHome;
import placebooks.client.ui.PlaceBookPreview;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class PlaceBookEditor implements EntryPoint
{
	public static final PlaceBookHistoryMapper historyMapper = GWT.create(PlaceBookHistoryMapper.class);
	private SimplePanel appWidget = new SimplePanel();

	@Override
	public void onModuleLoad()
	{
		Resources.STYLES.style().ensureInjected();

		final EventBus eventBus = new SimpleEventBus();
		final PlaceController placeController = new PlaceController(eventBus);

		// Start ActivityManager for the main widget with our ActivityMapper
		final ActivityMapper activityMapper = new PlaceBookActivityMapper(placeController);
		final ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);

		final GoogleAnalytics analytics = new GoogleAnalytics("UA-32206649-1");
		
		eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler()
		{
			@Override
			public void onPlaceChange(final PlaceChangeEvent event)
			{
				try
				{
					analytics.trackPage();					
				}
				catch(Exception e)
				{
					GWT.log(e.getMessage(), e);
				}
			}
		});

		activityManager.setDisplay(appWidget);

		appWidget.setHeight("100%");

		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

		Place defaultPlace = new PlaceBookHome();

		if (GWT.getHostPageBaseURL().endsWith("/a/view/"))
		{
			defaultPlace = new PlaceBookPreview(null, Window.Location.getPath()
					.substring(Window.Location.getPath().lastIndexOf('/') + 1));
		}

		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get().add(appWidget);
		// Goes to the place represented on URL else default place
		historyHandler.handleCurrentHistory();
	}
}
