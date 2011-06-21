package placebooks.client.ui.places;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.PlaceBookList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.SelectionChangeEvent;

public class PlaceBookAccountActivity extends AbstractActivity
{
	private final PlaceController controller;
	
	public PlaceBookAccountActivity(PlaceController controller)
	{
		super();
		this.controller = controller;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus)
	{
		final PlaceBookList list = new PlaceBookList();
		list.addSelectionHandler(new SelectionChangeEvent.Handler()
		{
			@Override
			public void onSelectionChange(final SelectionChangeEvent event)
			{
				final PlaceBookEntry entry = list.getSelection();
				controller.goTo(new PlaceBookEditorPlace(entry.getKey()));
			}
		});
		
		PlaceBookService.getShelf(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				final Shelf shelf = Shelf.parse(response.getText());
				list.setShelf(shelf);
			}
		});
		
		panel.setWidget(list);
	}
}
