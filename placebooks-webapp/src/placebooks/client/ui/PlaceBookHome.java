package placebooks.client.ui;

import placebooks.client.AbstractCallback;
import placebooks.client.PlaceBookService;
import placebooks.client.model.PlaceBookEntry;
import placebooks.client.model.Shelf;
import placebooks.client.ui.places.PlaceBookEditorNewPlace;
import placebooks.client.ui.places.PlaceBookEditorPlace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

public class PlaceBookHome extends Composite
{

	interface PlaceBookAccountUiBinder extends UiBinder<Widget, PlaceBookHome>
	{
	}

	private static PlaceBookAccountUiBinder uiBinder = GWT.create(PlaceBookAccountUiBinder.class);

	@UiField
	PlaceBookList placebookList;

	@UiField
	Panel userPanel;

	private Shelf shelf;

	public PlaceBookHome()
	{
		initWidget(uiBinder.createAndBindUi(this));

		Window.setTitle("Your PlaceBook Account");
	}

	public PlaceBookHome(final PlaceController controller)
	{
		initWidget(uiBinder.createAndBindUi(this));

		placebookList.addSelectionHandler(new SelectionChangeEvent.Handler()
		{
			@Override
			public void onSelectionChange(final SelectionChangeEvent event)
			{
				final PlaceBookEntry entry = placebookList.getSelection();
				if (entry.getKey().equals("new"))
				{
					controller.goTo(new PlaceBookEditorNewPlace(shelf.getUser()));
				}
				else
				{
					controller.goTo(new PlaceBookEditorPlace(entry.getKey()));
				}
			}
		});

		PlaceBookService.getShelf(new AbstractCallback()
		{
			@Override
			public void failure(final Request request)
			{
				showLogin();
			}

			@Override
			public void success(final Request request, final Response response)
			{
				try
				{
					setShelf(Shelf.parse(response.getText()));
				}
				catch (final Exception e)
				{
					failure(request);
				}
			}
		});
	}

	private void setShelf(final Shelf shelf)
	{
		this.shelf = shelf;
		placebookList.setShelf(shelf);

		userPanel.clear();
		userPanel.add(new PlaceBookUserDetails(shelf.getUser(), new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				showLogin();
			}
		}));
	}

	private void showLogin()
	{
		userPanel.clear();
		placebookList.setShelf(null);
		userPanel.add(new Login(new AbstractCallback()
		{
			@Override
			public void success(final Request request, final Response response)
			{
				try
				{
					setShelf(Shelf.parse(response.getText()));
				}
				catch (final Exception e)
				{
					failure(request);
				}
			}
		}));
	}
}